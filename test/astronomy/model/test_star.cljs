(ns astronomy.model.test-star
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [astronomy.conn.core :as conn.core]
   [astronomy.objects.star.m :as star.m]
   [astronomy.fixture :as fixture]))


(def test-db
  fixture/test-db1)


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