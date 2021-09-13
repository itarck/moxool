(ns astronomy.view.user.core
  (:require
   [posh.reagent :as p]
   [methodology.view.backpack :as v.backpack]
   [shu.three.spherical :as sph]
   [shu.astronomy.light :as shu.light]
   [astronomy.objects.clock.m :as m.clock]))


(defn LeftHandToolView [{:keys [clock] :as props} {:keys [conn]}]
  (let [astro-scene-1 @(p/pull conn '[{:astro-scene/camera [*]}] (get-in props [:astro-scene :db/id]))
        clock @(p/pull conn '[*] (:db/id clock))
        [r phi theta]  (apply sph/from-cartesian-coords (get-in astro-scene-1 [:astro-scene/camera :camera/position]))]
    [:div {:class "astronomy-lefthand"}
     [:p {:style {:font-size "14px"
                  :color "#aaa"}}
      [:div (str "距离原点：" (shu.light/semantic-distance-in-light-seconds (/ r 10000)))]
      [:div (str "世界时间：" (m.clock/utc-format-string (:clock/time-in-days clock)))]]]))


(defn RightHandToolView [{:keys [tool camera-control] :as props} {:keys [conn tool-library] :as env}]
  (let [tool @(p/pull conn '[*] (:db/id tool))
        view-fn (get tool-library (:entity/type tool))]
    (when view-fn
      [view-fn (merge props
                      {:tool tool
                       :spaceship-camera-control camera-control}) 
       env])))

(defn HUDView [props {:keys [conn hud-library] :as env}]
  (let [tool @(p/pull conn '[:entity/type :db/id] (get-in props [:tool :db/id]))
        view-fn (get hud-library (:entity/type tool))]
    (when view-fn
      [view-fn (merge props
                      {:tool tool})
       env])))

(defn MouseView [props {:keys [meta-atom conn]}]
  (let [mode (if meta-atom (:mode @meta-atom) :read-and-write)]
    (when (= mode :read-only)
      (let [user @(p/pull conn '[{:person/mouse [*]}] (get-in props [:user :db/id]))
            {:mouse/keys [page-x page-y]} (get user :person/mouse)]
        [:div {:class "methodology-mouse"}
         [:div {:style {:position "absolute"
                        :left page-x
                        :top page-y}}
          [:img {:src "/image/moxool/mouse.png"
                 :width "20px"}]]]))))


(defn UserView [{:keys [user camera-control astro-scene] :as props} {:keys [conn] :as env}]
  (let [user @(p/pull conn '[*] (:db/id user))
        backpack (:person/backpack user)]
    [:<>

     [LeftHandToolView {:astro-scene astro-scene
                        :user user
                        :clock (:astro-scene/clock astro-scene)} env]
     [v.backpack/BackPackView {:user user
                               :backpack backpack} env]
     (when (:person/right-tool user)
       [:<>
        [RightHandToolView {:user user
                            :astro-scene astro-scene
                            :tool (:person/right-tool user)
                            :camera-control camera-control} env]
        [HUDView {:user user
                  :astro-scene astro-scene
                  :tool (:person/right-tool user)} env]
        ]
       
       
       
       )

     [MouseView props env]]))




