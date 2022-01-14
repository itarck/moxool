(ns laboratory.plugin.test-backpack
  (:require
   [laboratory.dbs.dev :as dbs.dev]
   [laboratory.system.zero :as zero]
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [laboratory.test-helper :as helper]))


;; Unit test

;; sample data

(def initial-tx
  [#:user {:name "default"}
   #:backpack {:name "default"
               :user/_backpack [:user/name "default"]
               :cell (vec (for [i (range 12)]
                            #:backpack-cell{:index i}))}])

(def test-db1
  (helper/create-initial-db initial-tx))

;; spec 

(def spec 
  (helper/create-spec-unit))

(deftest test-spec-unit
  (let [spec (helper/create-spec-unit)]
    (testing "testing spec unit"
      (is (spec :valid? :db/id 345)))))


;; model 

(def model
  (helper/create-model-unit))

(deftest test-model-unit
  (let [model (helper/create-model-unit)]
    (testing "testing model unit"
      (is (= (model :backpack/find-nth-cell2
                    {:db test-db1
                     :backpack {:db/id [:backpack/name "default"]}
                     :nth-cell 0})
             {:db/id 3})))))

;; handle 

(deftest test-handle-unit
  (is true))

;; subscribe

(def test-db
  (dbs.dev/create-dev-db1))

(deftest test-subscribe-unit
  (testing "subscribe backpack/pull"
    (let [system (helper/create-event-system test-db)
          {::zero/keys [subscribe spec]} system]
      (is (spec :valid? :db/entity
                @(subscribe :backpack/pull {:id [:backpack/name "default"]}))))))



(deftest test-process-unit
  (is true))


(run-tests)


(comment

  (let [spec (helper/create-spec-unit)]
    (spec :valid? :user/backpack {:db/id 34}))


  (let [model (helper/create-model-unit)]
    (model :user/select-tool-tx {:user {:db/id 1}
                                 :tool {:db/id 2}}))

  (let [handle (helper/create-handle-unit)]
    (handle :backpack/click-cell))

  (let [handle (helper/create-handle-unit)]
    (handle :backpack/click-cell
            {:request/body {:user {:db/id 1}
                            :backpack {:db/id 2}
                            :cell {:db/id 3}
                            :active-cell {:db/id 3}}}))

  )