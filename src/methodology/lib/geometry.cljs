(ns methodology.lib.geometry
  (:require 
   [applied-science.js-interop :as j]
   [helix.core :refer [$]]
   ["three" :as three]
   [shu.three.spherical :as sph]
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [shu.goog.math :as gmath]))


(defn CircleComponent [props]
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