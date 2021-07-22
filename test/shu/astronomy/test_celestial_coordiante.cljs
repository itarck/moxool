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
  
  (let [v (v3/from-seq [-132.90985728030398 105.1462852927232 124.41104137711413])]
    (cc/from-vector v) )
  ;; => #:celestial-coordinate{:longitude -46.89168209025404, :latitude 30.009012722341744, :radius 210.2352936346976}

  
  ;; 
  )