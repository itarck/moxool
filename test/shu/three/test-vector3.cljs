(ns shu.three.test-vector3
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [shu.three.vector3 :as v3 :refer [vector3]]
   [shu.three.quaternion :as q]
   [shu.three.euler :as e]))


(def v1 (vector3 0 2 0))

(def q1 (q/from-euler (e/euler (/ Math/PI 2) 0 0)))

(def e1 (e/euler (/ Math/PI 2) 0 0))

(def va (v3/normalize (vector3 2 2 2)))


(deftest test-vector3
  (let [ov1 (v3/clone' v1)
        ova (v3/clone' va)
        oq1 q1]

    (is (=  1 (v3/length (v3/normalize v1))))
    (is (v3/almost-equal? (v3/apply-quaternion v1 q1) (v3/vector3 0 0 2)))

    (is (v3/almost-equal?
         (v3/apply-axis-angle v1 (v3/vector3 1 0 0) (/ Math/PI 2))
         (v3/vector3 0 0 2)))

    (is (v3/almost-equal?
         (v3/apply-euler v1 e1)
         (v3/vector3 0 0 2)))

    (is (v3/almost-equal? (v3/project-on-plane va (vector3 0 1 0)) (vector3 0.5773502691896258 0 0.5773502691896258)))


    (is (v3/almost-equal? (v3/normalize (v3/cross (vector3 1 1 0) v1))
                          (v3/vector3 0 0 1)))

    ;; 
    (is (v3/equals ov1 v1))
    (is (q/equals oq1 q1))
    (is (v3/almost-equal? va ova))))


(run-tests)


(comment

  (let [v (v3/from-seq '(92.56365879704339 104.88670990815883 156.92421328480273))]
    (v3/length v))
  
  ;; 
  )