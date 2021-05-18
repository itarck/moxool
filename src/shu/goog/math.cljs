(ns shu.goog.math
  (:require 
   [goog.math :as gmath]))

(defn angle
  ";; angle (x1, y1, x2, y2) → number"
  [x1 y1 x2 y2]
  (gmath/angle x1 y1 x2 y2))


(defn angle-difference
  ";; angleDifference (startAngle, endAngle) → number"
  [start-angle end-angle]
  (gmath/angleDifference start-angle end-angle))

 (defn angle-dx
   ";; angleDx (degrees, radius) → number"
   [degrees radius]
   (gmath/angleDx degrees radius)) 

(defn angle-dy
  ";; angleDy (degrees, radius) → number"
  [degrees radius]
  (gmath/angleDy degrees radius))

;; average (...var_args) → number
;; clamp (value, min, max) → number
;; isFiniteNumber (num) → boolean
;; isInt (num) → boolean
;; isNegativeZero (num) → boolean
;; lerp (a, b, x) → number

(defn lerp
  "Performs linear interpolation between values a and b. Returns the value between a and b proportional to x (when x is between 0 and 1. When x is outside this range, the return value is a linear extrapolation)."
  [a b x]
  (gmath/lerp a b x))

;; log10Floor (num) → number
;; longestCommonSubsequence<S, T> (array1, array2, opt_compareFn, opt_collectorFn) → Array< (S|T|null) >
;; modulo (a, b) → number

(defn nearly-equals
  ";; nearlyEquals (a, b, opt_tolerance) → boolean
opt_tolerance	number=
Optional tolerance range. Defaults to 0.000001. If specified, should be greater than 0."
  ([a b]
   (gmath/nearlyEquals a b))
  ([a b opt_tolerance]
   (gmath/nearlyEquals a b opt_tolerance)))


;; randomInt (a) → number
;; safeCeil (num, opt_epsilon) → number
;; safeFloor (num, opt_epsilon) → number
;; sampleVariance (...var_args) → number

(defn sign
  ";; sign (x) → number"
  [x]
  (gmath/sign x))

(defn standard-angle
  ";; standardAngle (angle) → number"
  [angle]
  (gmath/standardAngle angle))

(defn standard-angle-in-radians
  ";; standardAngleInRadians (angle) → number"
  [angle-in-radians]
  (gmath/standardAngleInRadians angle-in-radians))


;; standardDeviation (...var_args) → number
;; sum (...var_args) → number

(defn to-degree
  ";; toDegrees (angleRadians) → number"
  [angle-in-radians]
  (gmath/toDegrees angle-in-radians))

(defn to-radians
  ";; toRadians (angleDegrees) → number"
  [angle-in-degree]
  (gmath/toRadians angle-in-degree))

(defn uniform-random
  ";; uniformRandom (a, b) → number"
  [a b]
  (gmath/uniformRandom a b))


(comment

  (gmath/lerp 0 10 0.5)

  (gmath/standardAngle 2544)

  (gmath/standardAngleInRadians (* 3 Math/PI))

  (gmath/sign -342)

  (gmath/nearlyEquals 1 0.99999 1e-4)

  (gmath/angleDx 180 3)

;;   
  )