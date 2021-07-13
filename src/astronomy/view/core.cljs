(ns astronomy.view.core
  (:require
   ["react-three-fiber" :refer [Canvas]]
   [posh.reagent :as p]
   [methodology.model.user.person :as m.person]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.view.astro-scene :as v.astro-scene]
   [methodology.view.camera :as v.camera]
   [astronomy.view.user.core :as v.user]
   [astronomy.view.user.spaceship-camera-control :as v.spaceship]))


(defn RootView [props env]
  (let [{:keys [user-name scene-name]} props
        astro-scene {:db/id [:scene/name scene-name]}
        user {:db/id [:person/name user-name]}
        {:keys [meta-atom conn]} env
        mode (if meta-atom (:mode @meta-atom) :read-and-write)]
    ;; (println "load root view ???")
    [:<>
     [:> Canvas {:style {:background :black}
                 :shadowMap true}
      (when (m.astro-scene/sub-scene-name-exist? conn scene-name)
        (let [astro-scene-1 @(p/pull conn '[*] [:scene/name scene-name])
              user-1 @(p/pull conn '[*] (:db/id user))
              spaceship-camera-control (:person/camera-control user-1)]
          [:<>
           [v.camera/CameraView (:astro-scene/camera astro-scene-1) env]
           [v.astro-scene/AstroSceneView {:astro-scene astro-scene
                                          :spaceship-camera-control spaceship-camera-control} env]
           (when (= mode :read-and-write)
             [v.spaceship/SpaceshipCameraControlView {:spaceship-camera-control spaceship-camera-control
                                                      :astro-scene astro-scene-1} env])

           [:ambientLight {:intensity 0.3}]
           #_[:gridHelper {:args [10000 100 "gray" "gray"]}]]))]

     (when (m.person/sub-user-name-exist? conn user-name)
       (let [user-1 @(p/pull conn '[*] (:db/id user))
             astro-scene-1 @(p/pull conn '[*] [:scene/name scene-name])
             spaceship-camera-control (:person/camera-control user-1)]
         [v.user/UserView {:user user
                           :astro-scene astro-scene-1
                           :camera-control spaceship-camera-control} env]))]))

