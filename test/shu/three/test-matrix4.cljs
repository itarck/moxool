(ns shu.three.test-matrix4
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [shu.three.euler :as e]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.three.matrix4 :as mat4]
   [shu.arithmetic.number :as number]
   [shu.arithmetic.sequence :as shu.seq]))

(def eye (mat4/identity-matrix4))

(def p1 (v3/vector3 1 2 1))

(def q1 (q/from-euler (e/euler 0 0 (/ Math/PI 8))))

(def p2 (v3/vector3 3 5 3))

(def q2 (q/from-axis-angle (v3/normalize (v3/vector3 1 1 1)) (/ Math/PI 4)))

(def s1 (v3/vector3 2 3 5))
(def s3 (v3/vector3 1 1 1))

(def m1 (mat4/compose p1 q1 s1))

(def mp1 (apply mat4/make-translation p1))

(def mq1 (mat4/make-rotation-from-quaternion q1))

(def ms1 (apply mat4/make-scale s1))

(mat4/multiply m1 mp1)

(mat4/from-row-seq (range 16))

(mat4/from-col-seq (range 16))


(deftest test-matrix4
  (let [om1 (mat4/clone' m1)
        m (mat4/compose p1 q1 s1)
        [p2 q2 s2] (mat4/decompose m)]
    (is (shu.seq/almost-equal? (seq p1) (seq p2)))
    (is (shu.seq/almost-equal? (seq s1) (seq s2)))
    (is (shu.seq/almost-equal? (seq q1) (seq q2)))

    (is (= 1 (mat4/determinant (mat4/extract-rotation m1))))

    (is (number/almost-equal? (apply * s1) (mat4/determinant m1)))
    (is (mat4/almost-equals (mat4/multiply (mat4/invert m1) m1) (mat4/identity-matrix4)))
    (is (= (apply * s1) (mat4/determinant (mat4/scale (mat4/identity-matrix4) s1))))

    (is (mat4/equals
         (-> mq1
             (mat4/scale s1)
             (mat4/translate p1))
         (-> mq1
             (mat4/translate p1)
             (mat4/scale s1))))

    (is (mat4/equals m1 om1))))


(run-tests)


(comment 
  
(def p1 (v3/vector3 1 2 1))

(def q1 (q/from-euler (e/euler 0 0 (/ Math/PI 8))))

(def p2 (v3/vector3 3 5 3))

(def q2 (q/from-axis-angle (v3/normalize (v3/vector 1 1 1)) (/ Math/PI 4)))


  (let [p1 (v3/vector3 1 2 1)
        q1 (q/from-euler (e/euler 0 0 (/ Math/PI 8)))
        p2 (v3/vector3 3 5 3)
        q2 (q/from-axis-angle (v3/normalize (v3/vector3 1 1 1)) (/ Math/PI 4))
        s1 (v3/vector3 1 1 1)
        m1 (mat4/compose p1 q1 s1)
        m2 (mat4/compose p2 q2 s1)
        m3 (mat4/multiply m2 m1)
        p3 (v3/add p1 p2)
        q3 (q/multiply q2 q1)
        p4 (v3/vector3 1 0 0)]


    (println (-> p4
                 (v3/apply-matrix4 m1)
                 (v3/apply-matrix4 m2)))

    (println (-> p4
                 (v3/apply-matrix4 m3)))
    )
  
;;   
  )