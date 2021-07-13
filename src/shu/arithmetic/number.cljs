(ns shu.arithmetic.number
  (:require
   [goog.math :as gmath]))


(defn almost-equal?
  ";; nearlyEquals (a, b, opt_tolerance) → boolean
opt_tolerance	number=
Optional tolerance range. Defaults to 0.000001. If specified, should be greater than 0."
  ([a b]
   (gmath/nearlyEquals a b))
  ([a b opt_tolerance]
   (gmath/nearlyEquals a b opt_tolerance)))


(defn rand-sign []
  (if (< (rand) 0.5) -1 1))