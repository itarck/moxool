(ns shu.arithmetic.sequence
  (:require
   [shu.arithmetic.number :as shu.number]))


(defn almost-equal?
  ";; nearlyEquals (a, b, opt_tolerance) → boolean
opt_tolerance	number=
Optional tolerance range. Defaults to 0.000001. If specified, should be greater than 0."
  ([a b]
   (every? (fn [n] (shu.number/almost-equal? n 0.0)) (map - a b)))
  ([a b opt_tolerance]
   (every? (fn [n] (shu.number/almost-equal? n 0.0 opt_tolerance)) (map - a b))))



(comment 
  )