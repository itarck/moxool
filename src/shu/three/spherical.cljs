(ns shu.three.spherical
  (:require
   [applied-science.js-interop :as j]
   ["three" :as three]
   [shu.three.exception :refer [not-implemented-error mutable-error gen-exception]]
   [shu.general.core :as g]))


(defprotocol ISpherical
  (almost-equal? [s1 s2])
  (clone' [s]))


(extend-type three/Spherical
  Object
  (toString [v] (str (vec v)))

  ISeqable
  (-seq [v] (list (.-radius v) (.-phi v) (.-theta v)))

  ISeq
  (-first [v] (.-radius v))
  (-rest [v] (list (.-phi v) (.-theta v)))

  ILookup
  (-lookup
    ([v k] (-lookup v k nil))
    ([v k not-found]
     (case k
       0 (.-radius v)
       1 (.-phi v)
       2 (.-theta v)
       :radius (.-radius v)
       :phi (.-phi v)
       :theta (.-theta v)
       not-found)))

  ISpherical
  (almost-equal? [s1 s2]
    (g/almost-equal? s1 s2))

  (clone' [s1]
    (j/call s1 :clone')))


(defn spherical
  ([]
   (spherical 1 0 0))
  ([radius phi theta]
   (new three/Spherical radius phi theta)))

(defn from-seq [s]
  (let [[r p t] s]
    (spherical r p t)))

(defn from-cartesian-coords [x y z]
  (let [sp (spherical)]
    (j/call sp :setFromCartesianCoords x y z)
    sp))


(comment

  (from-cartesian-coords 0 1 0)
  
  )