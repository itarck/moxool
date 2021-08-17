(ns astronomy.objects.moon-orbit
  (:require
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [astronomy.model.ellipse-orbit :as m.ellipse-orbit]
   [astronomy.model.moon-orbit :as m.moon-orbit]
   [methodology.lib.geometry :as v.geo]))


(defn CelestialPositionLineView
  [{:keys [celestial]} env]
  (let [color (or (get-in celestial [:celestial/orbit :orbit/color])
                  "gray")]
    [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                   (v3/from-seq (map #(* 1.003 %) (:object/position celestial)))]
                          :color color}]))

(defn MoonOrbitView
  [{:keys [orbit celestial clock]} {:keys [conn] :as env}]
  (let [clock @(p/pull conn '[*] (:db/id clock))
        epoch-day (:clock/time-in-days clock)
        days (range (+ -60 epoch-day) (+ 0.2 epoch-day) 0.1)]
    [:<>
     [v.geo/LineComponent {:points (m.moon-orbit/cal-orbit-points-vectors orbit days)
                           :color (:orbit/color orbit)}]
     [CelestialPositionLineView {:celestial celestial} env]
     [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                    (m.moon-orbit/cal-perigee-vector orbit epoch-day)]
                           :color "#444"}]]))

(defn EllipseOrbitView 
  [{:keys [orbit]} env]
  [v.geo/LineComponent {:points (m.ellipse-orbit/cal-orbit-points-vectors orbit (* 10 360))
                        :color (:orbit/color orbit)}])


(defn CircleOrbitView
  [{:keys [orbit]} env]
  [v.geo/CircleComponent {:center [0 0 0]
                          :radius (:circle-orbit/radius orbit)
                          :axis (:circle-orbit/axis orbit)
                          :color (:orbit/color orbit)
                          :circle-points (* 360 20)}])


(defn MultiOrbitView
  [{:keys [orbit] :as props} env]
  (case (:orbit/type orbit)
    :moon-orbit [MoonOrbitView props env]
    :ellipse-orbit [EllipseOrbitView props env]
    :circle-orbit [CircleOrbitView props env]))

