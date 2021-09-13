(ns astronomy.model.test-astronomical-point
  (:require
   [cljs.test :refer-macros [deftest is run-tests testing]]
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [astronomy.objects.astronomical-point.m :as m.astro-point]
   [astronomy.conn.core :as conn]))


;; test model

(def apt-1
  (m.astro-point/astronomical-point 90 30))

(def apt-2
  (m.astro-point/from-position [0 0 100]))

(def apt-3
  (m.astro-point/from-position [0 0 -100]))


(deftest test-apt-data
  (is (= (v3/length (m.astro-point/cal-position-vector3 apt-1)) 31536000))
  (is (= (m.astro-point/get-longitude-and-latitude apt-2) [0 0]))
  (is (= (m.astro-point/get-longitude-and-latitude apt-3) [180 0]))
;;   
  )



;; test conn

(def test-conn (conn/create-narrow-conn-1!))

(def test-db @test-conn)

(def apt-11
  (m.astro-point/astronomical-point 90 30 "标记点1"))


(deftest test-apt-tx
  (testing "create-tx"
    (let [db-after (d/db-with test-db [apt-11])
          apt-after (d/pull db-after '[*] [:astronomical-point/name "标记点1"])]
      (is (=  apt-after
              {:db/id 62, :astronomical-point/coordinate #:db{:id 32}, :astronomical-point/latitude 30, :astronomical-point/longitude 90, :astronomical-point/name "标记点1", :astronomical-point/radius 31536000})))))


;; test sub


(deftest test-sub
  (let [test-conn2 (conn/create-narrow-conn-1!)
        _ (p/transact! test-conn2 [apt-1])]
    (is (= (m.astro-point/sub-all-ids-by-coordinate test-conn2 {:db/id [:coordinate/name "赤道天球坐标系"]})
           [62]))))


(run-tests)