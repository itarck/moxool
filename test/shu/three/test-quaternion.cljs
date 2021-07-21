(ns shu.three.test-quaternion
  (:require
   [applied-science.js-interop :as j]
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [shu.three.quaternion :as tq :refer [quaternion quatn]]
   [shu.three.vector3 :as v3]
   [shu.three.euler :as e]
   [shu.arithmetic.sequence :as shu.seq]
   [shu.three.matrix4 :as m4]))


(def q1 (quaternion 1 2 3 5))
(def q2 (quatn [1 2 3 5]))
(def q3 (tq/identity-quaternion))
(def q4 (quaternion 5 4 3 2))

(def nq1 (tq/normalize q1))
(def nq4 (tq/normalize q4))

(def nv3 (v3/normalize (v3/vector3 1 2 3)))

(def e1 (e/euler 1 2 3))

(def q5 (tq/from-euler e1))

(def q6 (tq/from-euler (e/euler 0 0 (/ Math/PI -8))))


(deftest test-quatn
  (let [oq1 (tq/clone' q1)
        oq2 (tq/clone' q2)]

    (is (tq/equals q1 q2))
    (is (tq/equals (tq/conjugate q1) (tq/quaternion -1 -2 -3 5)))

    (is (= 1 (tq/length (tq/normalize q1))))

    (is (tq/equals q3 (tq/multiply (tq/invert nq1) nq1)))
    (is (tq/equals (tq/multiply q1 (quaternion 1 0 0 0)) (quaternion 5 3 -2 -1)))


    (is (tq/equals (tq/slerp nq1 nq4 0) nq1))
    (is (tq/equals (tq/slerp nq1 nq4 1) nq4))

    (is (shu.seq/almost-equal?
         (seq (tq/from-unit-vectors (v3/vector3 1 0 0) (v3/vector3 0 1 0)))
         (seq (tq/from-axis-angle (v3/vector3 0 0 1) (/ Math/PI 2)))))

    (is (tq/almost-equals q6
                          (tq/from-rotation-matrix
                           (m4/make-rotation-from-quaternion q6))))
    ;; 
    (is (tq/equals q1 oq1))
    (is (tq/equals q2 oq2))))


(run-tests)


(comment

  (def q1 (tq/from-axis-angle (v3/vector3 1 0 0) (/ Math/PI 2)))
  q1
  ;; => #object[Quaternion [0.7071067811865475 0 0 0.7071067811865476]]

  (v3/apply-quaternion (v3/vector3 0 0 1) q1)

  (def q2 (tq/from-axis-angle (v3/vector3 0 0 1) (/ Math/PI 2)))

 (->
  (v3/vector3 0 0 1)
  (v3/apply-quaternion q1)
  (v3/apply-quaternion q2))
  
(-> (v3/vector3 0 0 1)
    (v3/apply-quaternion (tq/multiply q2 q1)))

  )