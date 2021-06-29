(ns astronomy.view.user.horizontal-coordinate-tool
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   [helix.core :refer [defnc $] :as h]
   ["react" :as react :refer [Suspense]]
   ["@material-ui/core" :as mt]
   ["@react-three/drei" :refer [Cylinder useTexture]]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   [astronomy.model.user.horizontal-coordinate-tool :as m.horizon]
   
   [astronomy.view.celestial-sphere-helper :as v.celestial-sphere]))


(defnc CompassComponent [props]
  (let [texture2 (useTexture "/image/moxool/compass.jpg")]
    ($ Cylinder {:args #js [0.000006 0.000006 0.0000005 50]
                 :position #js [0 -0.0000004 0]}
       ($ :meshBasicMaterial {:map texture2 :attach "material"}))))


(defn HorizontalCoordinateSceneView
  [props {:keys [conn] :as env}]
  (let [hct @(p/pull conn '[*] (get-in props [:object :db/id]))
        {:horizontal-coordinate-tool/keys [radius show-latitude? show-longitude? show-horizontal-plane? show-compass?]} hct
        spaceship-camera-control @(p/pull conn '[*] (get-in props [:spaceship-camera-control :db/id]))
        astro-scene @(p/pull conn '[*] (get-in props [:astro-scene :db/id]))
        coordinate @(p/pull conn '[*] (get-in astro-scene [:astro-scene/coordinate :db/id]))
        earth @(p/pull conn '[*] [:planet/name "earth"])]
    (when (= (:db/id earth) (get-in coordinate [:coordinate/track-position :db/id]))
      (let [p (m.spaceship/get-landing-position-in-scene spaceship-camera-control astro-scene)
            q2 (vec (m.horizon/cal-quaternion-on-sphere (:spaceship-camera-control/landing-position spaceship-camera-control)))]
        [:mesh {:position (:object/position earth)}
         [:mesh {:quaternion (:object/quaternion earth)}
          [:mesh {:quaternion q2
                  :position p}
           [:<>
            (when show-horizontal-plane?
              [:polarGridHelper {:args [radius 4 10 60 "green" "green"]}])

            [v.celestial-sphere/CelestialSphereHelperView {:radius radius
                                                           :show-latitude? show-latitude?
                                                           :show-longitude? show-longitude?
                                                           :longitude-interval 90
                                                           :longitude-color-map {:default "#060"
                                                                                 -180 "#0b0"}
                                                           :latitude-color-map {:default "#060"}}]
            (when show-compass?
              ($ Suspense {:fallback nil}
                 ($ CompassComponent)))

            [:> Cylinder {:args [0.0000001 0.0000006 0.00001 4]
                          :castShadow true}
             [:meshStandardMaterial {:color "black"}]]]]]]))))



(defn HorizontalCoordinateToolView [props {:keys [service-chan conn]}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        {:horizontal-coordinate-tool/keys [radius show-latitude? show-longitude? show-horizontal-plane? show-compass?]} tool
        spaceship-camera-control @(p/pull conn '[*] (get-in props [:spaceship-camera-control :db/id]))]
    [:div {:class "astronomy-righthand"}
     [:div {:class "astronomy-righthand-tool"}
      [:div.p-2
       [:div
        [:img {:src (:tool/icon tool)
               :class "astronomy-button"}]
        [:span {:style {:font-size "18px"
                        :font-weight "bold"}}
         (:tool/chinese-name tool)]]

       [:> mt/Grid {:container true :spacing 1}
        [:> mt/Grid {:item true :xs 12}
         [:> mt/Typography {:variant "subtitle1"}
          (if (= :surface-control (:spaceship-camera-control/mode spaceship-camera-control))
            "当前位于地表，可使用地平坐标系"
            "当前不在地表，无法使用地平坐标系")]]
        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示经度"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or (:horizontal-coordinate-tool/show-longitude? tool) false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :horizontal-coordinate-tool/change-show-longitude
                                                                    :detail {:tool tool
                                                                             :show? show?}}))))}]
         [:span "是"]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示纬度"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or (:horizontal-coordinate-tool/show-latitude? tool) false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :horizontal-coordinate-tool/change-show-latitude
                                                                    :detail {:tool tool
                                                                             :show? show?}}))))}]
         [:span "是"]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示指南针"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or (:horizontal-coordinate-tool/show-compass? tool) false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :horizontal-coordinate-tool/change-show-compass
                                                                    :detail {:tool tool
                                                                             :show? show?}}))))}]
         [:span "是"]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示地平面"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or (:horizontal-coordinate-tool/show-horizontal-plane? tool) false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :horizontal-coordinate-tool/change-show-horizontal-plane
                                                                    :detail {:tool tool
                                                                             :show? show?}}))))}]
         [:span "是"]]
         
         #_[:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "天球半径"]]
         #_[:> mt/Grid {:item true :xs 6}
          ($ mt/Slider
             {:style (clj->js {:color "#666"
                               :width "100px"})
              :value radius
              :onChange (fn [e value]
                          (go (>! service-chan #:event {:action :horizontal-coordinate-tool/change-radius
                                                        :detail {:tool tool
                                                                 :radius value}})))
              :step 0.001 :min 0.001 :max 0.01 :marks true
              :getAriaValueText identity
              :aria-labelledby "discrete-slider-restrict"
              :valueLabelDisplay "auto"})]
         
         ]]]]))