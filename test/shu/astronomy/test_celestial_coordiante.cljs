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