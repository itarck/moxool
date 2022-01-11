(ns laboratory.parts.test-backpack
  (:require
   [laboratory.dbs.dev :as dbs.dev]
   [fancoil.unit :as fu]
   [datascript.core :as d]
   [posh.reagent :as p]
   [laboratory.system.zero :as zero]
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [laboratory.test-system :as test-system]))

;; db

(def test-db
  (dbs.dev/create-dev-db1))

;; model test

(deftest test-spec-unit
  (let [spec (test-system/create-spec-unit)]
    (testing "testing spec unit"
      (is (spec :valid? :db/id 345)))))

(deftest test-model-unit
  (let [model (test-system/create-model-unit)]
    (testing "testing model unit"
      (is (= (model :user/select-tool-tx {:user {:db/id -1}
                                          :tool {:db/id -2}})
             [[:db/add -1 :user/right-tool -2]]))
      (is (= (model :user/drop-tool-tx {:user {:db/id -1}})
             [[:db.fn/retractAttribute -1 :user/right-tool]])))))


;; event test

(deftest test-subscribe-unit
  (testing "subscribe backpack/pull"
    (let [system (test-system/create-event-system {:initial-db test-db})
          {::fu/keys [subscribe spec]} system]
      (is (spec :valid? :db/entity
                @(subscribe :backpack/pull {:id [:backpack/name "default"]}))))))

(deftest test-handle-unit
  (let [handle (test-system/create-handle-unit)]
    (is (= (handle :backpack/click-cell
                   {:request/body {:user {:db/id 1}
                                   :backpack {:db/id 2}
                                   :cell {:db/id 3}
                                   :active-cell {:db/id 3}}})
           #:posh{:tx '([:db.fn/retractAttribute 2 :backpack/active-cell] [:db.fn/retractAttribute 1 :user/right-tool])}))))

(deftest test-process-unit
  (testing ":backpack/click-cell"
   (let [system (test-system/create-event-system {:initial-db test-db})
         {::fu/keys [process subscribe]} system
         bp @(subscribe :backpack/pull {:id [:backpack/name "default"]})
         user (first (:user/_backpack bp))
         cell (first (:backpack/cell bp))]
     (process :backpack/click-cell
              {:request/body {:user user
                              :backpack bp
                              :cell cell}})
     (is (= (:db/id cell)
            (-> @(subscribe :backpack/pull {:id [:backpack/name "default"]})
                :backpack/active-cell
                :db/id))))))

(run-tests)


(comment

  (let [spec (test-system/create-spec-unit)]
    (spec :valid? :user/backpack {:db/id 34}))


  (let [model (test-system/create-model-unit)]
    (model :user/select-tool-tx {:user {:db/id 1}
                                 :tool {:db/id 2}}))

  (let [handle (test-system/create-handle-unit)]
    (handle :backpack/click-cell))

  (let [handle (test-system/create-handle-unit)]
    (handle :backpack/click-cell
            {:request/body {:user {:db/id 1}
                            :backpack {:db/id 2}
                            :cell {:db/id 3}
                            :active-cell {:db/id 3}}}))

  (let [system (test-system/create-event-system {:initial-db test-db})
        {::fu/keys [process subscribe spec]} system
        bp @(subscribe :backpack/pull {:id [:backpack/name "default"]})
        user (first (:user/_backpack bp))
        cell (first (:backpack/cell bp))]
    (process :backpack/click-cell
             {:request/body {:user user
                             :backpack bp
                             :cell cell}})
    (= (:db/id cell)
       (:db/id (:backpack/active-cell @(subscribe :backpack/pull {:id [:backpack/name "default"]}))))))