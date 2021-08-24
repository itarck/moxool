(ns astronomy.model.test-terrestrial-coordinate
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.scripts.test-conn :as test-conn]
   [astronomy.model.astro-scene :as m.astro-scene]
   [methodology.model.object :as m.object]
   [astronomy.model.clock :as m.clock]
   [astronomy.objects.terrestrial-coordinate.m :as m.tc]))


;; test db

(def db-1 test-conn/test-db11)

(def tc-1
  (d/pull db-1 '[*] [:coordinate/name "地球坐标系"]))


(deftest test-terrestrial-coordinate-model
  (is (= (d/q m.tc/coordinate-names-q db-1)
         ["地球坐标系"]))
  (is (= (m.tc/update-position-and-quaternion-tx db-1 (:db/id tc-1))
         [{:db/id 36, :object/position [442.9497885783528 191.68192377598768 -88.40464973856325]
           :object/quaternion '(0 -0.6255422740012656 0 0.7801902738674237)}])))



(run-tests)


(m.tc/cal-min-distance db-1 tc-1)
