(ns shu.three.exception)


(def not-implemented-error (js/Error. "not implemented"))

(def mutable-error (js/Error. "it's mutable, deprecated"))


(defn gen-exception [s]
  (js/Error. s))