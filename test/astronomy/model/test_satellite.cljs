(ns astronomy.model.test-satellite
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [astronomy.scripts.test-conn :refer [create-test-conn!]]
   [astronomy.model.satellite :as m.satellite]))


(def conn (create-test-conn!))


(def wide-moon
  @(p/pull conn m.satellite/wide-selector 
           [:satellite/name "moon"]))


(deftest test-wide-moon 
  (is (get-in wide-moon [:celestial/clock :clock/time-in-days]) )
  (is (= (get-in wide-moon [:satellite/planet :planet/star :star/name]) "sun"))
  (is (> (v3/length (v3/from-seq (m.satellite/cal-world-position2 wide-moon))) 400)))


(run-tests)