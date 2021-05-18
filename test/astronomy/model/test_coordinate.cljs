(ns astronomy.model.test-coordinate
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.test-conn :refer [create-poshed-conn!]]
   [astronomy.model.coordinate :as m.coordinate]))


(def test-conn (create-poshed-conn!))

test-conn

(def ref1 @(p/pull test-conn '[*] [:celestial-ref/name "default"]))

(m.coordinate/cal-invert-matrix ref1)
;; => #object[Matrix4 
;;           1, 0, 0, 0
;;           0, 1, 0, 0
;;           0, 0, 1, 0
;;           0, 0, 0, 1]

