(ns shu.three.test-matrix3
  (:require
   [applied-science.js-interop :as j]
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [shu.general.core :as g]
   [shu.three.euler :as e]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.three.matrix3 :as mat3]
   [shu.three.matrix4 :as mat4]))

(def eye (mat3/identity-matrix3))

(def mat3-1 (mat3/matrix3 (range 9)))
(def mat4-1 (mat4/matrix4 (range 16)))

(def q6 (q/from-euler (e/euler 0 0 (/ Math/PI -8))))

(def m4a (mat4/make-rotation-from-quaternion q6))

(def m3a (mat3/from-matrix4 m4a))



(deftest test-matrix3
  (let [omat3-1 (mat3/clone' mat3-1)
        om3a (mat3/clone' m3a)]

    (is (mat3/almost-equals
         (mat3/from-matrix4 mat4-1)
         (mat3/matrix3 [0 1 2 4 5 6 8 9 10])))

    (is (mat3/equals (mat3/multiply (mat3/invert m3a) m3a) eye))

    (is (mat3/equals mat3-1
                     (-> mat3-1
                         (mat3/transpose)
                         (mat3/transpose))))

    ;; 
    (is (mat3/equals omat3-1 mat3-1))
    (is (mat3/equals om3a m3a))))


(run-tests)