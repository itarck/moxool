(ns astronomy.service.test-astronomical-coordinate
  (:require
   [cljs.test :refer-macros [deftest run-tests is]]
   [posh.reagent :as p]
   [astronomy.service.effect :as s.effect]
   [astronomy.scripts.test-conn :as test-conn]
   [astronomy.service.astronomical-coordinate-tool :as s.act]))


;; env

(def conn (test-conn/init-conn!))

(def props {})

(def env {:conn conn})

(def astronomical-coordinate
  @(p/pull conn '[*] [:coordinate/name "赤道天球坐标系"]))

;; test fn

(def handle-event s.act/handle-event)

(def handle-event! (s.effect/wrap-handle-event! handle-event))


;; tests 

(def event1 
  #:event {:action :astronomical-coordinate-tool/log
           :detail {:data "hello"}})

(def event2 
  #:event {:action :astronomical-coordinate-tool/change-show-longitude
           :detail {:astronomical-coordinate astronomical-coordinate
                    :show? false}})


(deftest test-parse-event
  (is (= (handle-event props env event1)
         #:effect{:action :log, :detail {:data "hello"}}))
  (is (= (handle-event props env event2)
         #:effect{:action :tx, :detail [{:db/id 34, :astronomical-coordinate/show-longitude? false}]}))
  (println "event2 tx-data: " (:tx-data (handle-event! props env event2))))


(run-tests)