(ns astronomy.model.test-coordinate
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [astronomy.conn.core :refer [create-basic-conn!]]
   [posh.reagent :as p]
   [astronomy.data.celestial :as d.celestial]
   [astronomy.data.coordinate :as d.coordinate]
   [astronomy.objects.coordinate.m :as m.coordinate]))


(def test-conn
  (let [conn (create-basic-conn!)]
    (p/transact! conn d.celestial/dataset1)
    (p/transact! conn d.coordinate/dataset1)
    conn))

(deftest test-coordinate
  (is (= (:entity/type @(p/pull test-conn '[*] [:coordinate/name "赤道天球坐标系"]))
         :astronomical-coordinate))
  (is (> (count
          (m.coordinate/sub-all-coordinate-names test-conn))
         0)))


(run-tests)

