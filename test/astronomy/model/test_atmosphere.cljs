(ns astronomy.model.test-atmosphere
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.test-conn :refer [create-poshed-conn! create-system-conn!]]
   [astronomy.model.atmosphere :as m.atm]))


(def conn (create-poshed-conn!))

(count (d/datoms @conn :eavt))


(def atmo1 (m.atm/sub-unique-one conn))


(m.atm/sun-position-vector atmo1)