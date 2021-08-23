(ns astronomy.model.test-celestial
  (:require
   [cljs.spec.alpha :as s]
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [datascript.core :as d]
   [shu.three.vector3 :as v3]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.objects.planet.m]
   [astronomy.scripts.test-conn :refer [test-db3 test-db11]]))


;; test model

(def clock
  (d/pull test-db11 '[*] [:clock/name "default"]))

(def earth
  (d/pull test-db11 '[* {:celestial/clock [*]}] [:planet/name "earth"]))

(def moon 
  (d/pull test-db11 '[*] [:satellite/name "moon"]))


(deftest test-celestial
  (let [length (->> (m.celestial/cal-position earth 0)
                    (v3/from-seq)
                    (v3/length))]
    (is (s/valid? :methodology/entity earth))
    (is (s/valid? :astronomy/celestial earth))
    (is (and (> length 480) (< length 510)))
    
    (is (= (m.celestial/cal-system-position-now test-db11 earth)
           [442.9497885783528 191.68192377598768 -88.40464973856325]))
    
    (is (= (m.celestial/cal-system-position-now test-db11 moon)
           [442.02879635340327 191.41923556121154 -89.43892902355343]))
    ))


(run-tests)


(comment


  ;; 
  )

