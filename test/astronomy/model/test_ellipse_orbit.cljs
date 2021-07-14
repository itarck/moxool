(ns astronomy.model.test-ellipse-orbit
  (:require
   [cljs-time.core :as t]
   [shu.calendar.epoch :as epoch]
   [shu.three.vector3 :as v3]
   [shu.goog.math :as gmath]
   [astronomy.model.ellipse-orbit :as m.ellipse-orbit]
   [astronomy.model.circle-orbit :as m.circle-orbit]))



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

(def moon-sample m.circle-orbit/moon-sample1)

(defn period-to-angular-velocity [period]
  (/ (* 2 Math/PI) period))


;; at epoch-days = 6782.349353981482
(def moon-sample2 
  #:circle-orbit {:start-position [-0.9689014153229576 -0.419281932759616 0.7255299499307593]
                  :radius 1.281
                  :axis [-0.34788975474381034 0.9347109327608587 0.07271994720581232]
                  :axis-precession-center [-0.3977771559301344 0.9174820620699532 0]
                  :axis-precession-velocity (period-to-angular-velocity -6798)
                  :angular-velocity (period-to-angular-velocity 27.321661)
                  :draconitic-angular-velocity (period-to-angular-velocity 27.212220815)

                  :orbit/type :circle-orbit
                  :orbit/color "white"
                  :orbit/show? true
                  :orbit/period 27.321661})

(def moon-sample3 
  #:circle-orbit {:start-position  [-0.7592958587703179 -0.20624249999135819 -1.0108881392377498]
                  :radius 1.281
                  :axis [-0.34885989419537267 0.9342903258325582 0.07347354134438353]
                  :axis-precession-center [-0.3977771559301344 0.9174820620699532 0]
                  :axis-precession-velocity (period-to-angular-velocity -6798)
                  :angular-velocity (period-to-angular-velocity 27.321661)
                  :draconitic-angular-velocity (period-to-angular-velocity 27.212220815)

                  :orbit/type :circle-orbit
                  :orbit/color "white"
                  :orbit/show? true
                  :orbit/period 27.321661})


earth-sample

(def date-time0 (t/date-time 2018 1 31 13 30))
(epoch/to-epoch-days date-time0)
;; => 6605.06324287037


(def date-time1 (t/date-time 2018 7 27 20 22))

(epoch/to-epoch-days date-time1)
;; => 6782.349353981482


(def earth-position (m.ellipse-orbit/cal-position-vector earth-sample 6782.349353981482))

;; => #object[Vector3 [-383.28087784940635 -165.8607828547091 287.0072761972445]]


(def moon-axis-vector (m.circle-orbit/cal-current-axis moon-sample 6782.349353981482))

;; => #object[Vector3 [-0.34788975474381034 0.9347109327608587 0.07271994720581232]]

(gmath/to-degree (v3/angle-to earth-position moon-axis-vector))

(def moon-position 
  (v3/multiply-scalar (v3/normalize earth-position) 1.281))
moon-position
;; => #object[Vector3 [-0.9689014153229576 -0.419281932759616 0.7255299499307593]]


(m.circle-orbit/cal-position moon-sample2 -6782.349353981482)
;; => #object[Vector3 [-0.7592958587703179 -0.20624249999135819 -1.0108881392377498]]

(m.circle-orbit/cal-position moon-sample3 6782.349353981482)
;; => #object[Vector3 [-0.968901415322958 -0.4192819327596161 0.7255299499307596]]


;; => #object[Vector3 [-0.7403339458794351 -0.3573542922560516 1.051992652460534]]


;; => #object[Vector3 [-383.28087784940635 -165.8607828547091 287.0072761972445]]

(gmath/to-degree (v3/angle-to (v3/from-seq [-0.7403339458794351 -0.3573542922560516 1.051992652460534])
                              (v3/from-seq [-383.28087784940635 -165.8607828547091 287.0072761972445])))
;; => 17.57878469081095


