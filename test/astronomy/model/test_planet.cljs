(ns astronomy.model.test-planet
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [posh.reagent :as p]
   [astronomy.conn.core :as conn.core]
   [astronomy.objects.planet.m :as planet.m]
   [astronomy.fixture :as fixture]))


(def test-db
  fixture/test-db1)

(def test-conn
  (conn.core/create-conn-from-db test-db))


(deftest test-planet-1
  (p/transact! test-conn [{:db/id [:satellite/name "moon"]
                           :object/scene [:scene/name "solar"]}])
  (is (= (+ 1 1) 2))
  (is (->
       (planet.m/sub-satellites test-conn {:db/id [:planet/name "earth"]})
       first
       :satellite/name)
      "moon"))


(run-tests)