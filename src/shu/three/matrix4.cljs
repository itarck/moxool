(ns shu.three.matrix4
  (:require
   [shu.general.core :as g]
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [applied-science.js-interop :as j]
   ["three" :as three]))


(defprotocol IMatrix4
  (almost-equals [m1 m2])
  (clone' [m])
  (determinant [m])
  (decompose [m] "Decomposes this matrix into it's position, quaternion and scale components.")
  (equals [m1 m2])
  (extract-rotation [m] "Extracts the rotation component of the supplied matrix m into this matrix's rotation component.")
  (invert [m] "Inverts this matrix, using the analytic method. You can not invert with a determinant of zero. If you attempt this, the method produces a zero matrix instead.")
  (multiply [m1 m2] "Sets this matrix to m1 * m2.")
  (scale [m v] "Multiplies the columns of this matrix by vector v.")
  (translate [m v] "Sets the position component for this matrix from vector v,")
  (set-position  [m v])
  (transpose [m1])

;; 
  )

(defn- switch-row-col [sq]
  (->>
   sq
   (partition 4)
   (apply map list)
   (apply concat)))


(defn matrix4
  ([]
   (three/Matrix4.))
  ([sq]
   (let [m (three/Matrix4.)]
     (j/call m :fromArray (clj->js sq))
     m)))

(defn from-col-seq [sq]
  (let [m (three/Matrix4.)]
    (j/call m :fromArray (clj->js sq))
    m))

(defn from-row-seq [sq]
  (let [m (three/Matrix4.)]
    (j/apply m :set (clj->js sq))
    m))

(extend-type three/Matrix4
  Object
  (toString [m]
    (apply str (map (fn [s] (str "\n       " (apply str (interpose ", " s))))
                    (partition 4 (:row-seq m)))))

  ISeqable
  (-seq [m] (:row-seq m))

  ISeq
  (-first [m] (first (seq m)))
  (-rest [m] (rest (seq m)))

  ILookup
  (-lookup
    ([m k] (-lookup m k nil))
    ([m k not-found]
     (cond
       (or (= k :elements) (= k :col-seq)) (seq (j/get m :elements))
       (= k :row-seq) (switch-row-col (seq (j/get m :elements)))
       (number? k) (get (vec m) k)
       :else nil)))

  IMatrix4

  (almost-equals [m1 m2]
    (g/almost-equal? (vec m1) (vec m2)))

  (clone' [m]
    (j/call m :clone))

  (determinant [m]
    (j/call m :determinant))

  (equals [m1 m2]
    (j/call m1 :equals m2))

  (decompose [m]
    (let [p1 (v3/vector3)
          q1 (q/quaternion)
          s1 (v3/vector3)]
      (j/call m :decompose p1 q1 s1)
      [p1 q1 s1]))

  (extract-rotation [m]
    (let [m1 (matrix4)]
      (j/call m1 :extractRotation m)
      m1))

  (invert [m]
    (let [m1 (clone' m)]
      (j/call m1 :invert)))

  (multiply [m1 m2]
    (let [mm (clone' m1)]
      (j/call mm :multiply m2)))

  (scale [m v]
    (let [mc (clone' m)]
      (j/call mc :scale v)))

  (translate [m v]
    (let [mc (clone' m)]
      (j/call mc :setPosition v)))

  (set-position [m v]
    (let [mc (clone' m)]
      (j/call mc :setPosition v)))

  (transpose [m1]
    (let [mm (clone' m1)]
      (j/call mm :transpose)))
  ;;    
  )


(defn identity-matrix4 []
  (let [m (matrix4)]
    (j/call m :identity)))

(defn compose [position euler-or-quaternion scale]
  (let [m (matrix4)]
    (cond
      (= (type euler-or-quaternion) three/Euler) (j/call m :compose position (q/from-euler euler-or-quaternion) scale)
      (= (type euler-or-quaternion) three/Quaternion) (j/call m :compose position euler-or-quaternion scale)
      :else  (throw (js/Error. "incorrect rotation type")))))

(defn make-rotation-x [theta]
  (let [m (matrix4)]
    (j/call m .makeRotationX theta)))

(defn make-rotation-y [theta]
  (let [m (matrix4)]
    (j/call m .makeRotationY theta)))

(defn make-rotation-z [theta]
  (let [m (matrix4)]
    (j/call m .makeRotationZ theta)))

(defn make-rotation-axis [axis theta]
  (let [m (matrix4)]
    (j/call m .makeRotationAxis axis theta)))

(defn make-basis [x-axis y-axis z-axis]
  (let [m (matrix4)]
    (j/call m .makeBasis x-axis y-axis z-axis)))

(defn make-rotation-from-euler [e]
  (let [m (matrix4)]
    (j/call m .makeRotationFromEuler e)))

(defn make-rotation-from-quaternion [q]
  (let [m (matrix4)]
    (j/call m .makeRotationFromQuaternion q)))

(defn make-scale [x y z]
  (let [m (matrix4)]
    (j/call m .makeScale x y z)))

(defn make-shear [x y z]
  (let [m (matrix4)]
    (j/call m .makeShear x y z)))

(defn make-translation [x y z]
  (let [m (matrix4)]
    (j/call m .makeTranslation x y z)))



(comment

  (def m1 (matrix4))



  )