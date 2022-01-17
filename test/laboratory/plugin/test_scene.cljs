(ns laboratory.plugin.test-scene
  (:require
   [datascript.core :as d]
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [cljs.spec.alpha :as s]
   [laboratory.system.zero :as zero]
   [laboratory.test-helper :as helper]))


;; data

(def tx
  [#:framework {:name "default"}
   #:scene{:name "default"
           :background "white"
           :framework/_scene [:framework/name "default"]}])


(def test-db
  (helper/create-initial-db tx))

(def scene-sample
  (d/pull test-db '[*] [:scene/name "default"]))


;; sepc

(def spec
  (helper/create-spec-unit))

(deftest test-spec-unit
  (testing "testing spec unit"
    (is (s/valid? :scene/scene scene-sample))))

;; model 

(def model
  (helper/create-model-unit))


(deftest test-model-unit
  (testing "testing :scene/create"
    (is (= (model :scene/create {})
           {:scene/name "default", :scene/background "white", :framework/_scene [:framework/name "default"]}))))


;; subscribe

(def system
  (helper/create-event-system test-db))

(def pconn
  (::zero/pconn system))

(def sub
  (::zero/subscribe system))


(run-tests)