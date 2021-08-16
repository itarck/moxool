(ns film2.modules.frame.m
  (:require 
   [datascript.core :as d]))


;; sample 

(def frame-1
  #:frame{:name "/temp/frame/solar-1.fra"
          :db-string ""
          :scene-type :solar})


;; schema

(def schema {:frame/name {:db/unique :db.unique/identity}})


;; transform



;; find

(def all-names-query
  '[:find [?name ...]
    :where [?id :frame/name ?name]])


(defn find-all-names [db]
  (d/q all-names-query db))