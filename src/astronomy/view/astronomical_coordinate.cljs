(ns astronomy.view.astronomical-coordinate
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   ["@react-three/drei" :refer [Html]]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [astronomy.model.const :as m.const]
   [methodology.lib.geometry :as v.geo]
   [astronomy.view.satellite :as v.satellite]
   [astronomy.component.celestial-sphere :as c.celestial-sphere]))


(defn EclipticMarksView [{:keys [radius color]}]
  [:mesh {:quaternion m.const/ecliptic-quaternion}
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
                           :axis m.const/ecliptic-axis
                           :color "orange"}]
   (let [points (c.celestial-sphere/gen-latitude-points 500 0 36)]
     [:mesh {:quaternion m.const/ecliptic-quaternion}
      [v.geo/PointsComponent {:points points
                              :size 40000
                              :color "orange"}]])
   [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                  (v3/multiply-scalar (v3/from-seq (:object/position earth)) -1)]
                         :color "orange"}]
   [EclipticMarksView {:radius 500
                       :color "orange"}]])


(defn AstronomicalCoordinateView
  [props {:keys [conn] :as env}]
  (let [tc @(p/pull conn '[*] (get-in props [:object :db/id]))
        {:astronomical-coordinate/keys [radius show-latitude? show-longitude? show-regression-line?
                                        show-latitude-0? show-longitude-0? show-ecliptic? show-lunar-orbit?]} tc
        earth @(p/pull conn '[*] [:planet/name "earth"])
        moon @(p/pull conn '[*] [:satellite/name "moon"])
        clock @(p/pull conn '[*] (-> (:celestial/clock earth) :db/id))]
    [:mesh {:position (:object/position tc)
            :quaternion (:object/quaternion tc)}
     [:<>
      [:> c.celestial-sphere/CelestialSphereComponent {:radius radius
                                    :longitude-interval 30
                                    :show-latitude? show-latitude?
                                    :show-longitude? show-longitude?
                                    :longitude-color-map {:default "#770000"}
                                    :latitude-color-map {:default "#770000"}}]

      (when show-latitude-0?
        [:<>
         [:> c.celestial-sphere/LatitudeComponent {:radius radius
                                                   :latitude 0
                                                   :color "red"}]
         [:> c.celestial-sphere/LongitudeMarksComponent {:radius radius
                                                         :color "red"}]])
      (when show-longitude-0?
        [:<>
         [:> c.celestial-sphere/LongitudeComponent {:radius radius
                                                    :longitude 0
                                                    :color "red"}]])

      (when show-regression-line?
        [:<>
         [:> c.celestial-sphere/LatitudeComponent {:radius radius
                                                   :latitude 23.4
                                                   :color "red"}]
         [:> c.celestial-sphere/LatitudeComponent {:radius radius
                                                   :latitude -23.4
                                                   :color "red"}]])

      (when show-ecliptic?
        [EclipticSceneView {:earth earth} env])

      (when show-lunar-orbit?
        [v.satellite/CelestialOrbitView {:orbit (:celestial/orbit moon)
                                         :celestial moon
                                         :clock clock} env])
      
      ;; 
      ]]))
