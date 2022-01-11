(ns laboratory.plugin.test-db
  (:require 
   [laboratory.dbs.dev :as dbs.dev]
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [laboratory.plugin.db]
   [laboratory.test-system :as test-system]))

;; fixture

(def test-db
  (dbs.dev/create-dev-db1))

;; 

(deftest test-add
  (is (= (+ 1 2) 3)))


(deftest test-spec-unit
  (let [spec (test-system/create-spec-unit)]
    (testing "testing spec-unit"
      (is (spec :valid? :db/id [:df/fd 34]))
      (is (spec :valid? :db/id 345))
      (is (spec :valid? :db/entity {:db/id [:df/fd 34]})))))


(deftest test-model-unit
  (let [model (test-system/create-model-unit)]
    (testing "testing model unit"
      (is (= (model :db/pull {:id [:user/name "default"]
                                  :db test-db})
             {:db/id 3, :user/backpack #:db{:id 4}, :user/name "default"})))))


(run-tests)


(comment 
  (let [model (test-system/create-model-unit)]
    (model :db/pull {:id [:user/name "default"]
                         :db test-db}))
  ;; => {:db/id 3, :user/backpack #:db{:id 4}, :user/name "default"}

  )