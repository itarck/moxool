(ns laboratory.plugin.test-backpack
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]
   [laboratory.dbs.dev :as dbs.dev]
   [laboratory.system.zero :as zero]
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [laboratory.test-helper :as helper]))


;; Unit test

;; sample data

(def initial-tx
  [#:user {:name "default"}
   #:backpack {:name "default"
               :cells (vec (for [i (range 12)]
                             #:backpack-cell{:index i}))}])

(def test-db1
  (helper/create-initial-db initial-tx))

;; spec 

(def spec
  (helper/create-spec-unit))

(deftest test-spec-unit
  (let [spec (helper/create-spec-unit)]
    (testing "testing spec unit"
      (is (spec :valid? :backpack/backpack
                #:backpack {:db/id -1
                            :name "default"
                            :cells [#:backpack-cell {:index 0
                                                     :db/id -10}
                                    #:backpack-cell {:index 1
                                                     :db/id -11}]})))))

;; model 

(def model
  (helper/create-model-unit))

(def backpack 
  (d/pull test-db1 '[*] [:backpack/name "default"]))

(deftest test-model-unit
  (testing ":backpack/pull"
    (is (s/valid? :backpack/backpack backpack))
    (is (= 12 (count (model :backpack/sorted-cells {:backpack backpack})))))

  (testing ":backpack/active and deactive cell"
    (let [cell (first (model :backpack/sorted-cells {:backpack backpack}))]
      (is (= (model :backpack/active-cell-tx {:backpack backpack :cell cell})
             [[:db/add 2 :backpack/active-cell 3]]))
      (is (= (model :backpack/deactive-cell-tx {:backpack backpack})
             [[:db.fn/retractAttribute 2 :backpack/active-cell]]))))
  
  (testing ":backpack/put-in-nth-cell-tx"
    (is (= (model :backpack/put-in-nth-cell-tx {:backpack backpack
                                                :nth-cell 0
                                                :tool {:db/id -100}})
           [[:db/add 3 :backpack-cell/tool -100]])))
  
  (testing ":backpack/init-tools-tx"
    (is (= [[:db/add 3 :backpack-cell/tool -100] [:db/add 4 :backpack-cell/tool -101]]
           (model :backpack/init-tools-tx {:backpack backpack
                                           :tools [{:db/id -100}
                                                   {:db/id -101}]})))))

;; handle 

(def handle 
  (helper/create-handle-unit test-db1))

(deftest test-handle-unit
  (testing "handle backpack/click-cell "
    (let [cell (first (model :backpack/sorted-cells {:backpack backpack}))]
      (is (=
           #:posh{:tx '([:db.fn/retractAttribute 2 :backpack/active-cell]
                        [:db/add 2 :backpack/active-cell 3])}
           (handle :backpack/click-cell {:request/body {:backpack backpack :cell cell}}))))))

;; subscribe

(def test-db
  (dbs.dev/create-dev-db1))

(def system
  (helper/create-event-system test-db))

(def subscribe
  (::zero/subscribe system))

(deftest test-subscribe-unit
  (testing "subscribe backpack/pull"
    (is (spec :valid? :backpack/backpack
              @(subscribe :backpack/pull {:entity {:db/id [:backpack/name "default"]}})))))


(deftest test-process-unit
  (is true))


(run-tests)

