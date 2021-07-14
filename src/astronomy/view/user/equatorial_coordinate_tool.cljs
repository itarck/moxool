(ns astronomy.view.user.equatorial-coordinate-tool
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]
   ["@react-three/drei" :refer [Html]]
   [shu.goog.math :as gmath]
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [methodology.lib.geometry :as v.geo]
   [astronomy.model.spin :as m.spin]
   [astronomy.view.celestial-sphere-helper :as v.celestial-sphere]
   [astronomy.view.satellite :as v.satellite]))

(def ecliptic-angle 23.439291111)

(def ecliptic-axis
  (let [ang ecliptic-angle]
    [(- (Math/sin (gmath/to-radians ang)))
     (Math/cos (gmath/to-radians ang))
     0]))

(def ecliptic-quaternion
  (let [ang ecliptic-angle]
    (vec (q/from-unit-vectors
          (v3/vector3 0 1 0)
          (v3/normalize (v3/from-seq [(- (Math/sin (gmath/to-radians ang)))
                                      (Math/cos (gmath/to-radians ang))
                                      0]))))))

(def lunar-axis [-0.34885989419537267 0.9342903258325582 0.07347354134438353])

(defn EclipticMarksView [{:keys [radius color]}]
  [:mesh {:quaternion ecliptic-quaternion}
   (for [i (range 36)]
     ^{:key i}
     [:> Html {:position (v3/from-spherical-coords
                          radius
                          (/ Math/PI 2)
                          (* i (/ 1 36) 2 Math/PI))
               :zIndexRange [0 0]
               :style {:color color
                       :font-size "14px"}}
      [:p (str (* i 10) "°")]])])

(defn EclipticSceneView [{:keys [earth]} env]
  [:<>
   [v.geo/CircleComponent {:center [0 0 0]
                           :radius 500
                           :axis ecliptic-axis
                           :color "orange"}]
   (let [points (v.celestial-sphere/gen-latitude-points 500 0 36)]
     [:mesh {:quaternion ecliptic-quaternion}
      [v.geo/PointsComponent {:points points
                              :size 40000
                              :color "orange"}]])
   [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                  (v3/multiply-scalar (v3/from-seq (:object/position earth)) -1)]
                         :color "orange"}]
   [EclipticMarksView {:radius 500
                       :color "orange"}]])

(defn EquatorialCoordinateSceneView
  [props {:keys [conn] :as env}]
  (let [ect @(p/pull conn '[*] (get-in props [:object :db/id]))
        {:equatorial-coordinate-tool/keys [radius show-latitude? show-longitude? show-regression-line?
                                           show-latitude-0? show-longitude-0? show-ecliptic?
                                           show-lunar-orbit?]} ect
        earth @(p/pull conn '[*] [:planet/name "earth"])
        moon @(p/pull conn '[*] [:satellite/name "moon"])
        clock @(p/pull conn '[*] (-> (:celestial/clock earth) :db/id))]
    [:<>
     [:mesh {:position (:object/position earth)}
      [:<>
       [v.celestial-sphere/CelestialSphereHelperView {:radius radius
                                                      :longitude-interval 30
                                                      :show-latitude? show-latitude?
                                                      :show-longitude? show-longitude?
                                                      :longitude-color-map {:default "#770000"}
                                                      :latitude-color-map {:default "#770000"}}]

       (when show-latitude-0?
         [:<>
          [v.celestial-sphere/LatitudeView {:radius radius
                                            :latitude 0
                                            :color "red"}]
          [v.celestial-sphere/LongitudeMarksView {:radius radius
                                                  :color "red"}]])
       (when show-longitude-0?
         [:<>
          [v.celestial-sphere/LongitudeView {:radius radius
                                             :longitude 0
                                             :color "red"}]])

       (when show-regression-line?
         [:<>
          [v.celestial-sphere/LatitudeView {:radius radius
                                            :latitude 23.4
                                            :color "red"}]
          [v.celestial-sphere/LatitudeView {:radius radius
                                            :latitude -23.4
                                            :color "red"}]])
       (when show-ecliptic?
         [EclipticSceneView {:earth earth} env])

       (when show-lunar-orbit?
         [v.satellite/CelestialOrbitView {:orbit (:celestial/orbit moon)
                                          :celestial moon
                                          :clock clock} env])

      ;; 
       ]]]
    ))


(defn EquatorialCoordinateToolView [props {:keys [service-chan conn]}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))]
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
        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示经度"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or (:equatorial-coordinate-tool/show-longitude? tool) false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :equatorial-coordinate-tool/change-show-longitude
                                                                    :detail {:tool tool
                                                                             :show? show?}}))))}]
         [:span "是"]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示纬度"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or (:equatorial-coordinate-tool/show-latitude? tool) false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :equatorial-coordinate-tool/change-show-latitude
                                                                    :detail {:tool tool
                                                                             :show? show?}}))))}]
         [:span "是"]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示天赤道"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or (:equatorial-coordinate-tool/show-latitude-0? tool) false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :equatorial-coordinate-tool/change-show-latitude-0
                                                                    :detail {:tool tool
                                                                             :show? show?}}))))}]
         [:span "是"]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示回归线"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or (:equatorial-coordinate-tool/show-regression-line? tool) false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :equatorial-coordinate-tool/change-show-regression-line
                                                                    :detail {:tool tool
                                                                             :show? show?}}))))}]
         [:span "是"]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示本初子午线"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or (:equatorial-coordinate-tool/show-longitude-0? tool) false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :equatorial-coordinate-tool/change-show-longitude-0
                                                                    :detail {:tool tool
                                                                             :show? show?}}))))}]
         [:span "是"]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示黄道"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or (:equatorial-coordinate-tool/show-ecliptic? tool) false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :equatorial-coordinate-tool/change-show-ecliptic
                                                                    :detail {:tool tool
                                                                             :show? show?}}))))}]
         [:span "是"]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示白道"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or (:equatorial-coordinate-tool/show-lunar-orbit? tool) false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :equatorial-coordinate-tool/change-show-lunar-orbit
                                                                    :detail {:tool tool
                                                                             :show? show?}}))))}]
         [:span "是"]]
         
         
         
         ]]]]
         
         
         ))