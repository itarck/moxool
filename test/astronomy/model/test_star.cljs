(ns astronomy.model.test-star
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [astronomy.conn.core :as conn.core]
   [posh.reagent :as p]
   [astronomy.objects.star.m :as star.m])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))


(def test-db
  (->>
   (read-resource "frame/test/test-db1.fra")
   (dt/read-transit-str)))


(def test-conn 
  (conn.core/create-conn-from-db test-db))



(deftest test-star-1
  (is (= (+ 1 1) 2))
  (is (->
       (star.m/sub-planets test-conn {:db/id [:star/name "sun"]})
       first
       :planet/name)
      "earth"))


(run-tests)