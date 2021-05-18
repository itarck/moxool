(ns shu.three.euler
  (:require
   [applied-science.js-interop :as j]
   ["three" :as three]))

(defprotocol IEuler
  (clone' [r])
  (equals [r1 r2])
  (reorder [r order])
  )


(extend-type three/Euler
  Object
  (toString [v] (str (vec v)))

  ISeqable
  (-seq [v] (list (.-x v) (.-y v) (.-z v) (.-order v)))

  ISeq
  (-first [v] (.-x v))
  (-rest [v] (list (.-y v) (.-z v) (.-order v)))

  ILookup
  (-lookup
    ([v k] (-lookup v k nil))
    ([v k not-found]
     (case k
       0 (.-x v)
       1 (.-y v)
       2 (.-z v)
       3 (.-order v)
       :x (.-x v)
       :y (.-y v)
       :z (.-z v)
       :order (.-order v)
       not-found)))

  IEuler

  (clone' [v1] (j/call v1 :clone))
  (equals [v1 v2]
    (j/call v1 :equals v2))

  (reorder [e order]
    (let [ec (clone' e)]
      (j/call ec :reorder order)))

  ;;    
  )


(defn euler
  ([] (three/Euler.))
  ([order] (three/Euler. 0 0 0 order))
  ([x y z] (three/Euler. x y z))
  ([x y z order] (three/Euler. x y z order)))

(defn from-seq
  ([sq]
   (from-seq sq "XYZ"))
  ([sq order]
   (let [[x y z] sq]
     (euler x y z order))))

(defn from-quaternion
  ([q]
   (from-quaternion q "XYZ"))
  ([q order]
   (let [e1 (euler order)]
     (j/call e1 :setFromQuaternion q order))))


(comment

  (def e1 (euler 1 2 3))
  (vec e1)
  (vec (reorder e1 :XZY))

  (j/call e1 :toVector3)

  )