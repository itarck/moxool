(ns laboratory.plugin.test-backpack
  (:require
   [laboratory.dbs.dev :as dbs.dev]
   [fancoil.unit :as fu]
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [laboratory.test-helper :as helper]))

;; db

(def test-db
  (dbs.dev/create-dev-db1))

;; model test

(deftest test-spec-unit
  (let [spec (helper/create-spec-unit)]
    (testing "testing spec unit"
      (is (spec :valid? :db/id 345)))))

(deftest test-model-unit
  (let [model (helper/create-model-unit)]
    (testing "testing model unit"
      (is (= (model :user/select-tool-tx {:user {:db/id -1}
                                          :tool {:db/id -2}})
             [[:db/add -1 :user/right-tool -2]]))
      (is (= (model :user/drop-tool-tx {:user {:db/id -1}})
             [[:db.fn/retractAttribute -1 :user/right-tool]])))))


;; event test

(deftest test-subscribe-unit
  (testing "subscribe backpack/pull"
    (let [system (helper/create-event-system {:initial-db test-db})
          {::fu/keys [subscribe spec]} system]
      (is (spec :valid? :db/entity
                @(subscribe :backpack/pull {:id [:backpack/name "default"]}))))))

(deftest test-handle-unit
  (let [handle (helper/create-handle-unit)]
    (is (= (handle :backpack/click-cell
                   {:request/body {:user {:db/id 1}
                                   :backpack {:db/id 2}
                                   :cell {:db/id 3}
                                   :active-cell {:db/id 3}}})
           #:posh{:tx '([:db.fn/retractAttribute 2 :backpack/active-cell] [:db.fn/retractAttribute 1 :user/right-tool])}))))

(deftest test-process-unit
  (testing ":backpack/click-cell"
   (let [system (helper/create-event-system {:initial-db test-db})
         {::fu/keys [process subscribe]} system
         bp @(subscribe :backpack/pull {:id [:backpack/name "default"]})
         cell (first (:backpack/cell bp))
         request {:request/body {:user (first (:user/_backpack bp))
                                 :backpack bp
                                 :cell cell}}
         _ (process :backpack/click-cell request)
         bp-after @(subscribe :backpack/pull {:id [:backpack/name "default"]})]
     (is (= (:db/id cell)
            (get-in bp-after [:backpack/active-cell :db/id]))))))


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