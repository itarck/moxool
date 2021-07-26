(ns astronomy.model.test-coordinate
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [astronomy.conn.core :refer [create-basic-conn!]]
   [posh.reagent :as p]
   [astronomy.data.coordinate :as d.coordinate]
   [astronomy.model.object :as m.object]
   [astronomy.model.coordinate :as m.coordinate]))


(def test-conn
  (let [conn (create-basic-conn!)]
    (p/transact! conn d.coordinate/dataset1)
    conn))


(m.coordinate/sub-all-coordinate-names conn)

