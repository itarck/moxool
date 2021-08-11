(ns astronomy.model.test-terrestrial-coordinate
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.scripts.test-conn :as test-conn]
   [astronomy.model.astro-scene :as m.astro-scene]
   [methodology.model.object :as m.object]
   [astronomy.model.clock :as m.clock]
   [astronomy.model.terrestrial-coordinate :as m.terrestrial-coordinate]))


;; test db

(def db-1 test-conn/test-db2)

(def tc-1
  (d/pull db-1 '[*] [:coordinate/name "地球坐标系"]))

(m.terrestrial-coordinate/update-position-and-quaternion-tx db-1 (:db/id tc-1))
;; => [{:db/id 34, :object/position [], :object/quaternion (0 -0.6255422740012656 0 0.7801902738674237)}]


(:object/position tc-1)
(:object/quaternion tc-1)

