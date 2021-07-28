(ns astronomy.model.test-satellite
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [astronomy.scripts.test-conn :as test-conn]
   [astronomy.model.satellite :as m.satellite]))


(def conn (test-conn/init-conn!))

(def wide-moon
  @(p/pull conn m.satellite/wide-selector 
           [:satellite/name "moon"]))


(deftest test-wide-moon
  (is (get-in wide-moon [:celestial/clock :clock/time-in-days]))
  (is (= (get-in wide-moon [:satellite/planet :planet/star :star/name]) "sun")))


(run-tests)