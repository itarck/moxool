(ns astronomy.model.test-atmosphere
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.conn.core :refer [create-empty-conn!]]
   [astronomy.data.celestial :as d.celestial]
   [astronomy.model.atmosphere :as m.atm]))


(def test-conn
  (let [conn (create-empty-conn!)]
    (d/transact! conn d.celestial/dataset3)
    (p/posh! conn)
    conn))

test-conn

(def atmo1 (m.atm/sub-unique-one test-conn))

atmo1
