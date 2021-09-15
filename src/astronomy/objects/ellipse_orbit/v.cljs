(ns astronomy.objects.ellipse-orbit.v
  (:require
   [shu.three.vector3 :as v3]
   [astronomy.objects.ellipse-orbit.m :as m.ellipse-orbit]
   [methodology.lib.geometry :as v.geo]))


(defn PositionLineView [{:keys [planet color]} env]
  (let [merged-color (or color
                         (get-in planet [:celestial/orbit :orbit/color])
                         "gray")]
    [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                   (v3/from-seq (map #(* 1.003 %) (:object/position planet)))]
                          :color merged-color}]))

(defn PeriapsisLineView [{:keys [orbit]} env]
  (let [periapsis-vector (m.ellipse-orbit/cal-periapsis-vector3 orbit)
        color "gray"]
    [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                   periapsis-vector]
                          :color color}]))

(defn AscendingNodeLineView [{:keys [orbit]} env]
  (let [ascending-node-vector3 (m.ellipse-orbit/cal-ascending-node-vector3 orbit)]
    [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                   ascending-node-vector3]
                          :color "gray"}]))


(defn EllipseOrbitView
  [{:keys [orbit] :as props} env]
  [:<>
   [v.geo/LineComponent {:points (m.ellipse-orbit/cal-orbit-points-vectors orbit (* 10 360))
                         :color (:orbit/color orbit)}]

   (when (:orbit/show-helper-lines? orbit)
     [:<>
      [PeriapsisLineView props env]
      [AscendingNodeLineView props env]])])

