(ns astronomy.model.test-star
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.test-conn :refer [create-poshed-conn!]]
   [astronomy.model.star :as m.star]))



(def test-conn (create-poshed-conn!))

test-conn

@(p/pull test-conn '[*] [:star/HR 1])



