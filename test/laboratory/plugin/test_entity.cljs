(ns laboratory.plugin.test-entity
  (:require
   [cljs.spec.alpha :as s]
   [cljs.spec.gen.alpha :as gen]
   [clojure.test.check.generators]
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [laboratory.plugin.entity]
   [laboratory.system :as sys]
   [laboratory.test-helper :as helper]))

;; data 

(def test-db 
  (helper/create-initial-db [{:user/name "default"}]))

;; spec

(def spec
  (helper/create-spec-unit))

(comment
  (spec :valid? :db/id "df")
  ;; => false

  (gen/generate (s/gen :db/id)))


(deftest test-spec-unit
  (let [spec (helper/create-spec-unit)]
    (testing "testing spec-unit"
      (is (spec :valid? :db/id [:df/fd 34]))
      (is (spec :valid? :db/id 345))
      (is (spec :valid? :entity/entity {:db/id [:df/fd 34]})))))

;; subscribe

(def system
  (helper/create-event-system test-db))

(def subscribe
  (::sys/subscribe system))

(deftest test-subscribe
  (testing "testing subscribe :pull and :q"
    (is (= @(subscribe :pull '[*] [:user/name "default"])
           {:db/id 1, :user/name "default"}))
    (is (= @(subscribe :q '[:find ?id .
                            :in $ ?name
                            :where [?id :user/name ?name]]
                       "default")
           1))))


(run-tests)

