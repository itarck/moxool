(ns astronomy.model.test-astronomical-coordinate
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [astronomy.scripts.test-conn :refer [create-test-conn!]]
   [posh.reagent :as p]
   [astronomy.model.object :as m.object]
   [astronomy.model.astronomical-coordinate :as m.astro-coor]))


(def conn (create-test-conn!))


(def astro-coor
  @(p/pull conn '[*] [:coordinate/name "赤道天球坐标系"]))


(p/transact! conn (m.astro-coor/update-position-tx @conn astro-coor))
;; => [{:db/id 24, :object/position [442.9497885783528 191.68192377598768 -88.40464973856325]}]

(def astro-coor2
  @(p/pull conn '[*] [:coordinate/name "赤道天球坐标系"]))

(m.object/cal-invert-matrix astro-coor2)