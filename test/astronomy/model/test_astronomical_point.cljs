(ns astronomy.model.test-astronomical-point
  (:require
   [cljs.test :refer-macros [deftest is run-tests testing]]
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.model.astronomical-point :as m.astro-point]
   [astronomy.conn.core :as conn]))


;; test model

(def apt-1
  (m.astro-point/astronomical-point [100 100 100]))

(def apt-2
  (m.astro-point/astronomical-point [0 0 100]))

(def apt-3
  (m.astro-point/astronomical-point [0 0 -100]))


(deftest test-apt-data
  (is (= apt-1 #:astronomical-point{:coordinate #:db{:id [:coordinate/name "赤道天球坐标系"]}, :point [100 100 100]}))
  (is (= (m.astro-point/get-longitude-and-latitude apt-1) [45 35.264389682754654]))
  (is (= (m.astro-point/get-longitude-and-latitude apt-2) [0 0]))
  (is (= (m.astro-point/get-longitude-and-latitude apt-3) [180 0]))
  
;;   
  )


;; test conn

(def test-conn (conn/create-narrow-conn-1!))

(def test-db @test-conn)

(def apt-11
  (assoc apt-1 :astronomical-point/name "标记点1"))


(deftest test-apt-tx
  (testing "create-tx"
    (let [db-after (d/db-with test-db [apt-11])
          apt-after (d/pull db-after '[*] [:astronomical-point/name "标记点1"])]
      (is (=  apt-after
              {:db/id 62, :astronomical-point/coordinate #:db{:id 32}, :astronomical-point/name "标记点1", :astronomical-point/point [100 100 100]}))))
  
  
  
  )


(run-tests)