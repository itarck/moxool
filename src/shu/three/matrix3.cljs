(ns shu.three.matrix3
  (:require
   [applied-science.js-interop :as j]
   ["three" :as three]
   [shu.general.core :as g]))

(defprotocol IMatrix3
  (almost-equals [m1 m2])
  (clone' [m])
  (determinant [m])
  (equals [m1 m2])
  (invert [m1] "Inverts this matrix, using the analytic method. You can not invert with a determinant of zero. If you attempt this, the method produces a zero matrix instead.")
  (multiply [m1 m2] "Sets this matrix to m1 * m2.")
  (transpose [m1] )
)


(extend-type three/Matrix3
  Object
  (toString [m] (str (vec m)))

  ISeqable
  (-seq [m] (apply list (:elements m)))

  ISeq
  (-first [m] (first (seq m)))
  (-rest [m] (rest (seq m)))

  ILookup
  (-lookup
    ([m k] (-lookup m k nil))
    ([m k not-found]
     (cond
       (= k :elements) (j/get m :elements)
       (number? k) (get (vec m) k)
       :else nil)))

  IMatrix3

  (almost-equals [m1 m2]
    (g/almost-equal? (vec m1) (vec m2)))

  (clone' [m]
    (j/call m :clone))

  (determinant [m]
    (j/call m :determinant))

  (equals [m1 m2]
    (j/call m1 :equals m2))

  (invert [m1]
    (let [mm (clone' m1)]
      (j/call mm :invert)))

  (multiply [m1 m2]
    (let [mm (clone' m1)]
      (j/call mm :multiply m2)))

  (transpose [m1]
    (let [mm (clone' m1)]
      (j/call mm :transpose)))
  ;;    
  )


(defn matrix3
  ([]
   (three/Matrix3.))
  ([sq]
   (let [m (three/Matrix3.)]
     (j/call m :fromArray (clj->js sq))
     m)))


(defn identity-matrix3 []
  (let [m (matrix3)]
    (j/call m :identity)))

(defn get-normal-matrix [m4]
  (let [m3 (matrix3)]
    (j/call m3 :getNormalMatrix m4)
    m3))

(defn from-matrix4 [m4]
  (let [m3 (matrix3)]
    (j/call m3 :setFromMatrix4 m4)))


(comment

  (matrix3 (range 10))

  (matrix3 (reverse (range 10)))

  (matrix3))