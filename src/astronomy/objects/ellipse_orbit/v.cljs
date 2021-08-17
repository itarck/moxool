(ns astronomy.objects.ellipse-orbit
  (:require
   [astronomy.model.ellipse-orbit :as m.ellipse-orbit]
   [methodology.lib.geometry :as v.geo]))


(defn EllipseOrbitView
  [{:keys [orbit]} env]
  [v.geo/LineComponent {:points (m.ellipse-orbit/cal-orbit-points-vectors orbit (* 10 360))
                        :color (:orbit/color orbit)}])

