(ns laboratory.parts.test-entity
  (:require 
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [fancoil.base :as base]
   [laboratory.parts.entity]))


(deftest test-add
  (is (= (+ 1 2) 3)))


(deftest test-spec
  (is (base/spec {} :valid? :db/id [:df/fd 34]))
  (is (base/spec {} :valid? :db/id 345))
  (is (base/spec {} :valid? :entity/model {:db/id [:df/fd 34]}))
)


(run-tests)

