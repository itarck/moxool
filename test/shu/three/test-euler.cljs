(ns shu.three.test-euler
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [shu.three.euler :as e]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.general.core :as g]))


(def e1 (e/euler 1 2 3))

e1
;; => #object[Euler [1 2 3 "XYZ"]]

