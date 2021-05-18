(ns shu.three.math-utils
  (:require
   [applied-science.js-interop :as j]
   ["three" :as three]))


(def math-utils three/MathUtils)

(defn clamp
  "value — Value to be clamped.
   min — Minimum value.
   max — Maximum value.
   Clamps the value to be between min and max."
  [value min max]
  (j/call math-utils :clamp value min max))

(defn deg-to-rad
  ".degToRad ( degrees : Float ) : Float
    Converts degrees to radians."
  [degree]
  (j/call math-utils :degToRad degree))

(defn euclidean-modulo
  ";; .euclideanModulo ( n : Integer, m : Integer ) : Integer
   ;; n, m - Integers
   ;; Computes the Euclidean modulo of m % n, that is:
   ;; ( ( n % m ) + m ) % m "
  [n m]
  (j/call math-utils .euclideanModulo n m))

(defn generate-UUID
  ";; .generateUUID ( ) : UUID
   ;; Generate a UUID (universally unique identifier)."
  []
  (j/call math-utils .generateUUID))

(defn is-power-of-two
  ";; .isPowerOfTwo ( n : Number ) : Boolean
   ;; Return true if n is a power of 2."
  [n]
  (j/call math-utils .isPowerOfTwo n))

(defn inverse-lerp
  ";; .inverseLerp ( x : Float, y : Float, value : Float ) : Float
   ;; x - Start point.
   ;; y - End point.
   ;; value - A value between start and end.
   ;; Returns the percentage in the closed interval [0, 1] of the given value between the start and end point.
   "
  [x y value]
  (j/call math-utils .inverseLerp x y value))

(defn lerp
  "
;; .lerp ( x : Float, y : Float, t : Float ) : Float
;; x - Start point.
;; y - End point.
;; t - interpolation factor in the closed interval [0, 1].

;; Returns a value linearly interpolated from two known points based on the given interval - t = 0 will return x and t = 1 will return y.
"
  [x y t]
  (j/call math-utils .lerp x y t))


(defn damp
  ";; .damp ( x : Float, y : Float, lambda : Float, dt : Float ) : Float
;; x - Current point.
;; y - Target point.
;; lambda - A higher lambda value will make the movement more sudden, and a lower value will make the movement more gradual.
;; dt - Delta time in seconds.

;; Smoothly interpolate a number from x toward y in a spring-like manner using the dt to maintain frame rate independent movement. For details, see Frame rate independent damping using lerp.
"
  [x y lambda dt]
  (j/call math-utils .damp x y lambda dt))

(defn map-linear
  ";; .mapLinear ( x : Float, a1 : Float, a2 : Float, b1 : Float, b2 : Float ) : Float
;; x — Value to be mapped.
;; a1 — Minimum value for range A.
;; a2 — Maximum value for range A.
;; b1 — Minimum value for range B.
;; b2 — Maximum value for range B.

;; Linear mapping of x from range [a1, a2] to range [b1, b2].
"
  [x a1 a2 b1 b2]
  (j/call math-utils :mapLinear x a1 a2 b1 b2))


(defn pingpong
  ";; .pingpong ( x : Float, length : Float ) : Float
;; x — The value to pingpong.
;; length — The positive value the function will pingpong to. Default is 1.

;; Returns a value that alternates between 0 and length : Float.
"
  [x length]
  (j/call math-utils :pingpong x length))


(defn ceil-power-of-two
  ";; .ceilPowerOfTwo ( n : Number ) : Integer
;; Returns the smallest power of 2 that is greater than or equal to n.
"
  [n]
  (j/call math-utils .ceilPowerOfTwo n))

(defn floor-power-of-two
  ";; .floorPowerOfTwo ( n : Number ) : Integer
;; Returns the largest power of 2 that is less than or equal to n.
"
  [n]
  (j/call math-utils .floorPowerOfTwo n))


(defn rad-to-deg
  ";; .radToDeg ( radians : Float ) : Float
   ;; Converts radians to degrees."
  [radians]
  (j/call math-utils .radToDeg radians))


(defn rand-float
  ";; .randFloat ( low : Float, high : Float ) : Float
   ;; Random float in the interval [low, high]."
  [low high]
  (j/call math-utils .randFloat low high))

(defn rand-float-spread
  ";; .randFloatSpread ( range : Float ) : Float
;; Random float in the interval [- range / 2, range / 2].
"
  [range]
  (j/call math-utils .randFloatSpread range))

(defn rand-int
  ";; .randInt ( low : Integer, high : Integer ) : Integer
;; Random integer in the interval [low, high].
"
  [low high]
  (j/call math-utils .randInt low high))

(defn seeded-random
  "
;; .seededRandom ( seed : Integer ) : Float
;; Deterministic pseudo-random float in the interval [0, 1]. The integer seed is optional.
"
  ([]
   (j/call math-utils .seededRandom))
  ([seed]
   (j/call math-utils .seededRandom seed)))


(defn smooth-step
  ";; .smoothstep ( x : Float, min : Float, max : Float ) : Float
;; x - The value to evaluate based on its position between min and max.
;; min - Any x value below min will be 0.
;; max - Any x value above max will be 1.

;; Returns a value between 0-1 that represents the percentage that x has moved between min and max, but smoothed or slowed down the closer X is to the min and max.
;; See Smoothstep for details.
"
  [x min max]
  (j/call math-utils .smoothstep x min max))


(defn smoother-step
  ";; .smootherstep ( x : Float, min : Float, max : Float ) : Float
;; x - The value to evaluate based on its position between min and max.
;; min - Any x value below min will be 0.
;; max - Any x value above max will be 1.

;; Returns a value between 0-1. A variation on smoothstep that has zero 1st and 2nd order derivatives at x=0 and x=1.
"
  [x min max]
  (j/call math-utils .smootherstep x min max))


;; .setQuaternionFromProperEuler ( q : Quaternion, a : Float, b : Float, c : Float, order : String ) : null
;; q - the quaternion to be set
;; a - the rotation applied to the first axis, in radians
;; b - the rotation applied to the second axis, in radians
;; c - the rotation applied to the third axis, in radians
;; order - a string specifying the axes order: 'XYX', 'XZX', 'YXY', 'YZY', 'ZXZ', or 'ZYZ'

;; Sets quaternion q from the intrinsic Proper Euler Angles defined by angles a, b, and c, and order order.
;; Rotations are applied to the axes in the order specified by order: rotation by angle a is applied first, then by angle b, then by angle c. Angles are in radians.




(comment

  (deg-to-rad 34)
  (euclidean-modulo 100 8)

  (generate-UUID)
  (is-power-of-two 6443)
  
  (inverse-lerp 0 10 6)

  (lerp 0 10 0.6)

  (map-linear 4 0 10 10 20)

  (rand-float 0 34)
  
;;   
  )