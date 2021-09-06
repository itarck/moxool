(ns astronomy.objects.circle-orbit.v
  (:require
   [methodology.lib.geometry :as v.geo]
   [shu.three.vector3 :as v3]))


(defn PositionLineView
  [{:keys [celestial]} env]
  (let [color (or (get-in celestial [:celestial/orbit :orbit/color])
                  "gray")]
    [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                   (v3/from-seq (map #(* 1.05 %) (:object/position celestial)))]
                          :color color}]))


(defn CircleOrbitView
  [{:keys [orbit] :as props} env]
  [:<>
   [v.geo/CircleComponent {:center [0 0 0]
                           :radius (:circle-orbit/radius orbit)
                           :axis (:circle-orbit/axis orbit)
                           :color (:orbit/color orbit)
                           :circle-points (* 360 20)}]
   [PositionLineView props env]])

