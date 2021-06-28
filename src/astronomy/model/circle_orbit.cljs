(ns astronomy.model.circle-orbit
  (:require 
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]))


;; data

(def schema {})


(defn period-to-angular-velocity [period]
  (/ (* 2 Math/PI) period))

(def sample1
  #:circle-orbit {:start-position [0 0 -1.281]
                  :radius 1.281
                  :axis [0 1 0]
                  :angular-velocity (period-to-angular-velocity 27)})


(defn cal-position [orbit days]
  (let [{:circle-orbit/keys [start-position axis angular-velocity]} orbit
        position-angle (* angular-velocity days)
        position (v3/apply-axis-angle
                  (v3/from-seq start-position)
                  (v3/normalize (v3/from-seq axis))
                  position-angle)]
    position))

(defn cal-tilt-quaternion [circle-orbit]
  (q/from-unit-vectors
   (v3/vector3 0 1 0)
   (v3/normalize (v3/from-seq (:circle-orbit/axis circle-orbit)))))


(comment 
  (cal-position sample1 365)
  (cal-tilt-quaternion sample1)
  )