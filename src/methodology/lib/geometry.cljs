(ns methodology.lib.geometry
  (:require
   [applied-science.js-interop :as j]
   [helix.core :refer [$]]
   ["three" :as three]
   ["@react-three/drei" :refer [Cone]]
   [shu.three.spherical :as sph]
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [shu.goog.math :as gmath]))


(defn CircleComponent 
  "
   props sample
   {:center [0 0 0]
    :radius 500
    :axis m.const/ecliptic-axis
    :color \"orange\"}
   "
  [props]
  (let [merged-props (merge {:color "gray"
                             :circle-points 3600}
                            props)
        {:keys [center radius axis color circle-points]} merged-props
        lineGeometry (three/BufferGeometry.)
        qt (seq (q/from-unit-vectors
                 (v3/vector3 0 1 0)
                 (v3/normalize (v3/from-seq axis))))]
    (j/call lineGeometry :setFromPoints (clj->js (for [i (range 0 360.0001 (/ 360 circle-points))]
                                                   (v3/from-spherical-coords radius
                                                                             (gmath/to-radians 90)
                                                                             (gmath/to-radians i)))))
    [:mesh {:position center
            :quaternion qt}
     ($ "line" {:geometry lineGeometry}
        ($ "lineBasicMaterial" {:linewidth 1
                                :color color
                                :linecap "round"
                                :linejoin "round"}))]))



(defn LineComponent [{:keys [points color] :as props}]
  (let [lineGeometry (three/BufferGeometry.)]
    (j/call lineGeometry :setFromPoints (clj->js points))
    ($ "line" {:geometry lineGeometry}
       ($ "lineBasicMaterial" {:linewidth 1
                               :color color
                               :linecap "round"
                               :linejoin "round"}))))


(defn ArrowLineComponent
  "
   {:start [0 0 0]
    :direction [1 0 0]
    :length 500
    :arrow-size 10
    :color \"orange\"}
   "
  [props]
  (let [{:keys [start direction length arrow-size color]} (merge {:color "gray"} props)
        axis (v3/vector3 0 length 0)
        start-v (v3/from-seq start)
        end-v (v3/add (v3/from-seq start) axis)
        q1 (q/from-unit-vectors (v3/from-seq [0 1 0])
                                (v3/normalize (v3/from-seq direction)))]
    [:mesh {:quaternion (seq q1)
            :position start-v}
     [LineComponent {:points [start-v end-v]
                     :color color}]
     [:> Cone {:args [(* 0.1 arrow-size) arrow-size 16]
               :position axis}
      [:meshBasicMaterial {:color color}]]]))


(defn CrossComponent
  "
   props sample
   {:center [0 0 0]
    :radius 500
    :axis m.const/ecliptic-axis
    :color \"orange\"}
   "
  [props]
  (let [merged-props (merge {:color "gray"}
                            props)
        {:keys [center radius axis color]} merged-props
        qt (seq (q/from-unit-vectors
                 (v3/vector3 0 1 0)
                 (v3/normalize (v3/from-seq axis))))
        points (for [i (range 0 360.0001 (/ 360 4))]
                 (v3/from-spherical-coords radius
                                           (gmath/to-radians 90)
                                           (gmath/to-radians i)))]

    [:mesh {:position center
            :quaternion qt}
     (for [p points]
       ^{:key (str p)}
       [LineComponent {:points [p (v3/vector3 0 0 0)]
                       :color color}])]))


(defn PointsComponent [{:keys [points size color] :as props}]
  #_(println "mount stars")
  (let [positions (let [positions #js []]
                    (doseq [point points]
                      (let [[x y z] (seq point)]
                        (j/push! positions x)
                        (j/push! positions y)
                        (j/push! positions z)))
                    (js/Float32Array.  positions))]
    ($ "points"
       ($ "bufferGeometry"
          ($ "bufferAttribute" {:attach-object #js ["attributes" "position"]
                                :count         (count points)
                                :array         positions
                                :item-size     3}))
       ($ "pointsMaterial" {:size             size
                            :size-attenuation true
                            :color            color
                            :transparent      true
                            :opacity          1
                            :fog              false}))))