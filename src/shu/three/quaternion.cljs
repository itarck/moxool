(ns shu.three.quaternion
  (:require
   [applied-science.js-interop :as j]
   ["three" :as three]
   [shu.three.exception :refer [not-implemented-error mutable-error gen-exception]]
   [shu.general.core :as g]))


(defprotocol IThreeQuanternion
  (almost-equals [q1 q2])
  (angle-to [q1 q2] "Returns the angle between this quaternion and quaternion q in radians.")
  (clone' [q] "Creates a new Quaternion with identical x, y, z and w properties to this one.")
  (conjugate [q] "Returns the rotational conjugate of this quaternion. The conjugate of a quaternion represents the same rotation in the opposite direction about the rotational axis.")
  (equals [q1 q2] "Quaternion that this quaternion will be compared to.")
  (dot [q1 q2] "Calculates the dot product of quaternions v and this one.")
  (invert [q] "Inverts this quaternion - calculates the conjugate. The quaternion is assumed to have unit length.")
  (length [q] "Computes the Euclidean length (straight-line length) of this quaternion, considered as a 4 dimensional vector.")
  (length-sq [q] "Computes the squared Euclidean length (straight-line length) of this quaternion, considered as a 4 dimensional vector. This can be useful if you are comparing the lengths of two quaternions, as this is a slightly more efficient calculation than length().")
  (normalize [q] "Normalizes this quaternion - that is, calculated the quaternion that performs the same rotation as this one, but has length equal to 1.")
  (multiply [q1 q2] "Sets this quaternion to a x b.")
  (rotate-towards [q1 q2 step] "q - The target quaternion. step - The angular step in radians.")
  (slerp [q1 q2 t] "q2 - The other quaternion rotation. t - interpolation factor in the closed interval [0, 1].")

  ;; 
  )


(extend-type three/Quaternion

  Object
  (toString [q] (str (vec q)))

  ISeqable
  (-seq [q] (list (.-x q) (.-y q) (.-z q) (.-w q)))

  ISeq
  (-first [q] (.-x q))
  (-rest [q] (list (.-y q) (.-z q) (.-w q)))

  ILookup
  (-lookup
    ([v k] (-lookup v k nil))
    ([v k not-found]
     (case k
       0 (.-x v)
       1 (.-y v)
       2 (.-z v)
       3 (.-w v)
       :x (.-x v)
       :y (.-y v)
       :z (.-z v)
       :w (.-w v)
       not-found)))

  IThreeQuanternion
  (almost-equals [q1 q2]
    (g/almost-equal? q1 q2))

  (angle-to
    [q1 q2]
    (j/call q1 :angleTo q2))

  (clone' [this]
    (j/call this :clone))

  (conjugate [q]
    (let [q2 (clone' q)]
      (j/call q2 :conjugate)))

  (equals [this b]
    (j/call this :equals b))

  (dot [q1 q2]
    (j/call q1 :dot q2))

  (invert [q]
    (let [qc (clone' q)]
      (j/call qc :invert)))

  (length [q]
    (j/call q :length))

  (length-sq [q]
    (j/call q :lengthSq))

  (normalize [q]
    (let [qc (clone' q)]
      (j/call qc :normalize)))

  (multiply [q1 q2]
    (let [q (clone' q1)]
      (j/call q :multiply q2)))

  (rotate-towards [q1 q2 step]
    (let [qc (clone' q1)]
      (j/call qc :rotateTowards q2 step)
      qc))

  (slerp [q1 q2 t]
    (let [q1c (clone' q1)]
      (j/call q1c :slerp q2 t)))

  ;; 
  )

;; functions

(defn quaternion
  ([]
   (new three/Quaternion))
  ([x y z w]
   (new three/Quaternion x y z w)))

(defn quatn [v]
  (let [[x y z w] (seq v)]
    (quaternion x y z w)))

(def from-seq quatn)

(defn identity-quaternion []
  (let [q (quaternion)]
    (j/call q :identity)
    q))

(defn from-array
  ([a] (let [qc (three/Quaternion)] 
         (j/call qc  :fromArray a)))
  ([a offset] (let [qc (three/Quaternion)]
                (j/call qc  :fromArray a) offset)))

(defn from-axis-angle
  "Sets this quaternion from rotation specified by axis and angle.
   Adapted from the method here.
   Axis is assumed to be normalized, angle is in radians."
  [axis angle]
  (let [q (quaternion)]
    (j/call q :setFromAxisAngle axis angle)))

(defn from-unit-vectors
  "Sets this quaternion to the rotation required to rotate direction vector vFrom to direction vector vTo."
  [v-from v-to]
  (let [q (quaternion)]
    (j/call q .setFromUnitVectors v-from v-to)))

(defn from-euler
  "Sets this quaternion from the rotation specified by Euler angle."
  [e]
  (let [q (quaternion)]
    (j/call q :setFromEuler e)))

(defn from-rotation-matrix
  "m - a Matrix4 of which the upper 3x3 of matrix is a pure rotation matrix (i.e. unscaled).
   Sets this quaternion from rotation component of m."
  [m4]
  (let [q (quaternion)]
    (j/call q :setFromRotationMatrix m4)))

;; comments

(comment

  (def q1 (quaternion 1 2 3 5))
  (def q2 (quaternion 5 6 5 0))
  (def q3 (identity-quaternion))


  (def nq1 (normalize q1))
  (multiply (invert nq1) nq1)

  (slerp q1 q2 0.5)

  q1

  ;; 
  )
  