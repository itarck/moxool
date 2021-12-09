(ns astronomy.objects.moon-orbit.v
  (:require
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [methodology.lib.geometry :as v.geo]
   [astronomy.lib.const :as const]
   [astronomy.objects.coordinate.m :as m.coordinate]
   [astronomy.objects.moon-orbit.m :as moon-orbit.m]))


(defn NorthPoleView
  [{:keys [orbit epoch-day]}]
  (let [axis (moon-orbit.m/cal-north-pole-vector3 orbit epoch-day)
        north-pole-on-sph (moon-orbit.m/cal-north-pole-on-astronomical-sphere orbit epoch-day)
        props {:center north-pole-on-sph
               :radius (* 0.005 const/astronomical-sphere-radius)
               :axis axis
               :color "white"
               :circle-points 60}]
    [:<>
     [v.geo/CrossComponent props]]))


(defn NorthAxisView
  "
   {:orbit
   :moon 
   :epoch-days
   }
   "
  [{:keys [orbit epoch-day]} {:keys [conn]}]
  (let [axis (moon-orbit.m/cal-north-pole-vector3 orbit epoch-day)
        props {:start [0 0 0]
               :direction axis
               :length (* 1.2 (:moon-orbit/semi-major-axis orbit))
               :arrow-size (* 0.1 (:moon-orbit/semi-major-axis orbit))
               :color "gray"}]
    [v.geo/ArrowLineComponent props]))


(defn CelestialPositionLineView
  [{:keys [celestial]} env]
  (let [color (or (get-in celestial [:celestial/orbit :orbit/color])
                  "gray")]
    [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                   (v3/from-seq (map #(* 1.05 %) (:object/position celestial)))]
                          :color color}]))

(defn MoonOrbitView
  [{:keys [orbit celestial clock]} {:keys [conn] :as env}]
  (let [clock @(p/pull conn '[*] (:db/id clock))
        epoch-day (:clock/time-in-days clock)
        days (range (+ -60 epoch-day) (+ 0.2 epoch-day) 0.1)]
    [:<>
     [v.geo/LineComponent {:points (moon-orbit.m/cal-orbit-points-vectors orbit days)
                           :color (:orbit/color orbit)}]
     [CelestialPositionLineView {:celestial celestial} env]

     (when (:moon-orbit/show-helper-lines? orbit)
       [:<>
        [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                       (moon-orbit.m/cal-perigee-vector orbit epoch-day)]
                              :color "#666"}]
        [NorthPoleView {:orbit orbit
                        :epoch-day epoch-day}]
        [NorthAxisView {:orbit orbit
                        :epoch-day epoch-day} env]])]))
