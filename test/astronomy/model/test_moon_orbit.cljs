(ns astronomy.model.test-moon-orbit
  (:require
   [cljs-time.core :as t]
   [shu.astronomy.date-time :as dt]
   [shu.three.vector3 :as v3]
   [shu.goog.math :as gmath]
   [astronomy.model.ellipse-orbit :as m.ellipse-orbit]
   [astronomy.model.moon-orbit :as mo :refer [moon-sample1]]
   ))


(=
 (mo/cal-current-longitude-of-the-ascending-node moon-sample1 0)
 (+ (mo/cal-current-longitude-of-the-ascending-node moon-sample1 6798) 360))

(=
 (mo/cal-current-argument-of-periapsis moon-sample1 0)
 (- (mo/cal-current-argument-of-periapsis moon-sample1 3233) 360))

(def earth-sample
  #:ellipse-orbit{:semi-major-axis 499.0052919
                  :eccentricity 0.01671022
                  :inclination-in-degree 0.00005
                  :longitude-of-the-ascending-node-in-degree -11.26064
                  :argument-of-periapsis-in-degree 114.20783
                  :mean-anomaly-in-degree 357.51716
                  :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree 365.256363004)

                  :orbit/type :ellipse-orbit
                  :orbit/period 365.256363004
                  :orbit/color "green"
                  :orbit/show? true})

(def earth-position (m.ellipse-orbit/cal-position-vector earth-sample 6782.349353981482))

(def earth-position2 (m.ellipse-orbit/cal-position-vector earth-sample 6605.06324287037))

earth-position2
;; => #object[Vector3 [338.5767737386333 146.515537063253 -324.9770686262562]]

(mo/cal-position-vector moon-sample1 6605.06324287037)
;; => #object[Vector3 [0.8407439572379062 0.3645083560100463 -0.9253792643425993]]


(gmath/to-degree (v3/angle-to (mo/cal-position-vector moon-sample1 6605.06324287037)
                              (m.ellipse-orbit/cal-position-vector earth-sample 6605.06324287037)))
;; => 1.2584784914030833

(gmath/to-degree (v3/angle-to (mo/cal-position-vector moon-sample1 6782.349353981482)
                              (m.ellipse-orbit/cal-position-vector earth-sample 6782.349353981482)))
;; => 13.410982219077093






