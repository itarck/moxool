(ns laboratory.plugin.test-entity
  (:require
   [laboratory.dbs.dev :as dbs.dev]
   [cljs.spec.alpha :as s]
   [cljs.spec.gen.alpha :as gen]
   [clojure.test.check.generators]
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [laboratory.plugin.entity]
   [laboratory.test-helper :as helper]))

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


;; model 

(def test-db
  (dbs.dev/create-dev-db1))



(run-tests)

