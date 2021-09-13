(ns astronomy.model.test-atmosphere
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.conn.core :refer [create-basic-conn!]]
   [astronomy.data.celestial :as d.celestial]
   [astronomy.objects.atmosphere.m :as m.atm]))


(def test-conn
  (let [conn (create-basic-conn!)]
    (d/transact! conn d.celestial/dataset3)
    conn))

test-conn

(def atmo1 (m.atm/sub-unique-one test-conn))

atmo1
