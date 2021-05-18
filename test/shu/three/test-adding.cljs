(ns shu.three.test-adding
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   ))



(deftest adding
  (is (= (+ 1 1) 2)))

(run-tests)