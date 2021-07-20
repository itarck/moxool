(ns astronomy.model.test-coordinate
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [astronomy.scripts.test-conn :refer [create-test-conn!]]
   [posh.reagent :as p]
   [astronomy.model.object :as m.object]
   [astronomy.model.coordinate :as m.coordinate]))


(def conn (create-test-conn!))


(m.coordinate/sub-all-coordinate-names conn)

