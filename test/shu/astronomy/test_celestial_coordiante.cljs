(ns shu.astronomy.test-celestial-coordinate
  (:require
   [shu.three.vector3 :as v3]
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [shu.astronomy.celestial-coordinate :as cc]))


(def sample
  #:celestial-coordinate {:longitude 30
                          :latitude 0
                          :radius 1})

(def sample2
  #:celestial-coordinate {:longitude 60
                          :latitude 23
                          :radius 1})


(deftest test-celestial-coordinate
  (is (= (cc/right-ascension sample) 2))
  (is (= (cc/declination sample) 40))

  (is (cc/almost-equal?
       (cc/from-unit-vector (cc/to-unit-vector sample))
       sample))

  (is (v3/almost-equal?
       (v3/normalize (cc/cal-position (cc/celestial-coordinate 30 60) 2500))
       (cc/to-unit-vector (cc/celestial-coordinate 30 60)))))




(run-tests)


(comment 
  
  (let [v (v3/from-seq '(54.659866380199894 163.49055161809267 119.63234325255185))]
    (cc/from-vector v) )
  

  (cc/distance-in-degree (cc/celestial-coordinate 121.21, 19.31) 
                         (cc/celestial-coordinate 135.83, 17.09))
  ;; => 14.060109694233343

  ;; 
  )