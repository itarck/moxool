(ns shu.geometry.angle
  (:require
   [goog.math :as gmath]))


;; #:angle{:degree 300}


(defn standard-angle-in-degrees
  ";; standardAngle (angle) → number
   Normalizes an angle to be in range [0-360). Angles outside this range will be normalized to be the equivalent angle with that range."
  [angle]
  (gmath/standardAngle angle))


(defn standard-angle-in-radians
  ";; standardAngleInRadians (angle) → number
   Normalizes an angle to be in range [0-2*PI). Angles outside this range will be normalized to be the equivalent angle with that range."
  [angle-in-radians]
  (gmath/standardAngleInRadians angle-in-radians))

(defn to-degrees
  ";; toDegrees (angleRadians) → number"
  [angle-in-radians]
  (gmath/toDegrees angle-in-radians))

(defn to-radians
  ";; toRadians (angleDegrees) → number"
  [angle-in-degree]
  (gmath/toRadians angle-in-degree))
