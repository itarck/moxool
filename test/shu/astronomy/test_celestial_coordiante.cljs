(ns shu.astronomy.test-celestial-coordinate
  (:require
   [shu.three.vector3 :as v3]
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [shu.astronomy.celestial-coordinate :as cc]))


(def sample
  #:celestial-coordinate {:longitude 30
                          :latitude 40})


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
  ;; => #:celestial-coordinate{:longitude 24.55560088562043, :latitude 51.18336724365963, :radius 209.830310044059}

  ;; => #:celestial-coordinate{:longitude -119.49298021035723, :latitude 51.14121936716526, :radius 209.82759182933466}

  ;; => #:celestial-coordinate{:longitude -46.89168209025404, :latitude 30.009012722341744, :radius 210.2352936346976}

  
  ;; 
  )