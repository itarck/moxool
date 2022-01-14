(ns laboratory.plugin.test-user
  (:require
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [cljs.spec.alpha :as s]
   [laboratory.system.zero :as zero]
   [laboratory.test-helper :as helper]
   [posh.reagent :as p]))


;; data

(def user-sample
  {:db/id -1
   :user/name "default"
   :user/backpack #:backpack {:name "default"
                              :active-cell {:db/id -2}
                              :cell [#:backpack-cell{:db/id -2
                                                     :index 0
                                                     :tool {:db/id -3}}
                                     #:backpack-cell{:index 1}]}})

(def tool-sample
  {:db/id -3})

(def test-db 
  (helper/create-initial-db 
   [user-sample
    tool-sample]))


;; sepc

(def spec 
  (helper/create-spec-unit))

(deftest test-spec-unit
  (let [spec (helper/create-spec-unit)]
    (testing "testing spec unit"
      (is (spec :valid? :db/id 345)
          (s/valid? :user/entity user-sample)))))

;; model 

(deftest test-model-unit
  (let [model (helper/create-model-unit)]
    (testing "testing model unit"
      (is (= (model :user/select-tool-tx {:user {:db/id -1}
                                          :tool {:db/id -2}})
             [[:db/add -1 :user/right-tool -2]]))
      (is (= (model :user/drop-tool-tx {:user {:db/id -1}})
             [[:db.fn/retractAttribute -1 :user/right-tool]])))))


;; subscribe

(helper/create-event-system test-db)

(def system 
  (helper/create-event-system test-db))

(def pconn 
  (::zero/pconn system))

(def sub 
  (::zero/subscribe system))

@(sub :user/right-hand-tool {:user {:db/id [:user/name "default"]}})



(run-tests)


(comment

  (let [spec (helper/create-spec-unit)]
    (spec :valid? :user/backpack {:db/id 34}))


  (let [model (helper/create-model-unit)]
    (model :user/select-tool-tx {:user {:db/id 1}
                                 :tool {:db/id 2}}))

)

