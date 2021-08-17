(ns astronomy.objects.circle-orbit
  (:require
   [methodology.lib.geometry :as v.geo]))



(defn CircleOrbitView
  [{:keys [orbit]} env]
  [v.geo/CircleComponent {:center [0 0 0]
                          :radius (:circle-orbit/radius orbit)
                          :axis (:circle-orbit/axis orbit)
                          :color (:orbit/color orbit)
                          :circle-points (* 360 20)}])

