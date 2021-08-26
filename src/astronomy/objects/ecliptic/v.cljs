(ns astronomy.objects.ecliptic.v
  (:require
   [shu.three.vector3 :as v3]
   [posh.reagent :as p]
   ["@react-three/drei" :refer [Html]]
   [astronomy.model.const :as m.const]
   [methodology.lib.geometry :as v.geo]
   [astronomy.component.celestial-sphere :as c.celestial-sphere]))



(defn EclipticMarksView [{:keys [radius color]}]
  [:mesh {:quaternion m.const/ecliptic-quaternion}
   (for [i (range 12)]
     ^{:key i}
     [:> Html {:position (v3/from-spherical-coords
                          radius
                          (/ Math/PI 2)
                          (* i (/ 1 12) 2 Math/PI))
               :zIndexRange [0 0]
               :style {:color color
                       :font-size "14px"}}
      [:p (str (* i 30) "°")]])])


(defn EclipticSceneView [props {:keys [conn]}]
  (let [ecliptic @(p/pull conn '[*] (get-in props [:object :db/id]))]
    (when (:ecliptic/show? ecliptic)
      (let [earth @(p/pull conn '[*] [:planet/name "earth"])]
        [:mesh {:position (:object/position earth)}
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
         #_[EclipticMarksView {:radius 500
                             :color "orange"}]]))))
