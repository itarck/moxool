(ns astronomy.view.core
  (:require
   ["react-three-fiber" :refer [Canvas]]
   [astronomy.view.astro-scene :as v.astro-scene]
   [methodology.view.camera :as v.camera]
   [astronomy.view.user.core :as v.user]
   [astronomy.view.user.spaceship-camera-control :as v.spaceship]
   ))



(defn RootView [props env]
  (let [{:keys [astro-scene user camera camera-control]} props
        {:keys [scene-atom]} env
        mode (if scene-atom (:mode @scene-atom) :read-and-write)]
    [:<>
     [:> Canvas {:style {:background :black}}
      [v.camera/CameraView camera env]
      [v.astro-scene/AstroSceneView astro-scene env]
      (when (= mode :read-and-write)
        [v.spaceship/SpaceshipCameraControlView camera-control env])
      [:ambientLight {:intensity 0.05}]
      ;; [:gridHelper {:args [20 20 "gray" "gray"]}]
      ]

     [v.user/UserView {:user user
                       :camera-control camera-control} env]]))
