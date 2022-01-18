(ns laboratory.plugin.test-object
  (:require
   [cljs.spec.alpha :as s]
   [cljs.spec.gen.alpha :as gen]
   [clojure.test.check.generators]
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [datascript.core :as d]
   [laboratory.plugin.entity]
   [laboratory.system :as sys]
   [laboratory.test-helper :as helper]
   [laboratory.app :as app]))

;; data 

(def test-db
  (helper/create-initial-db
   [#:scene {:name "default"}
    #:object {:name "object1"
              :position [0 0 0]
              :rotation [0 0 0]
              :scale [1 1 1]
              :scene {:db/id [:scene/name "default"]}}]))

(def object-sample 
  (d/pull test-db '[*] [:object/name "object1"]))

;; spec

(def spec
  (helper/create-spec-unit))

(deftest test-spec-unit
  (testing "tesing object spec"
    (is (spec :valid? :object/object object-sample))))


;; subscribe

(def system
  (helper/create-event-system test-db))

(def subscribe
  (::sys/subscribe system))

(deftest test-subscribe)


;; view

(comment

  (let [tx [#:scene {:name ::scene}
            #:framework {:name "default"
                         :scene [:scene/name ::scene]}
            #:object {:name "object1"
                      :position [0 0 0]
                      :rotation [0 0 0]
                      :scale [1 1 3]
                      :scene {:db/id [:scene/name ::scene]}}]]
    (app/homies :transact! tx))
  )


(run-tests)