(ns laboratory.parts.test-entity
  (:require 
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [laboratory.parts.entity]
   [laboratory.test-system :as test-system]))


(deftest test-add
  (is (= (+ 1 2) 3)))


(deftest test-spec-unit
  (let [spec (test-system/create-spec-unit)]
    (testing "testing spec-unit"
      (is (spec :valid? :db/id [:df/fd 34]))
      (is (spec :valid? :db/id 345))
      (is (spec :valid? :entity/model {:db/id [:df/fd 34]})))))


(run-tests)
