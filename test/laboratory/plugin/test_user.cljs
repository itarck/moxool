(ns laboratory.plugin.test-user
  (:require
   [datascript.core :as d]
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [cljs.spec.alpha :as s]
   [laboratory.system.zero :as zero]
   [laboratory.test-helper :as helper]))


;; data

(def tx
  [{:db/id -1
    :user/name "default"
    :user/backpack #:backpack {:db/id -11
                               :name "default"
                               :active-cell {:db/id -2}
                               :cells [#:backpack-cell{:db/id -2
                                                       :index 0
                                                       :tool {:db/id -3}}
                                       #:backpack-cell{:db/id -3
                                                       :index 1}]}}])


(def test-db 
  (helper/create-initial-db tx))

(def user-sample 
  (d/pull test-db '[*] [:user/name "default"]))


;; sepc

(def spec 
  (helper/create-spec-unit))

(deftest test-spec-unit
  (testing "testing spec unit"
    (is (spec :valid? :db/id 345))
    (is (s/valid? :user/user user-sample))))

;; model 

(def model
  (helper/create-model-unit))

(deftest test-model-unit)


;; subscribe

(def system 
  (helper/create-event-system test-db))

(def pconn 
  (::zero/pconn system))

(def sub 
  (::zero/subscribe system))

@(sub :user/right-hand-tool {:user {:db/id [:user/name "default"]}})
;; => nil





(run-tests)

