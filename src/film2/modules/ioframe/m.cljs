(ns film2.modules.ioframe.m
  (:require
   [datascript.core :as d]
   [astronomy.system.mini :as mini]
   [astronomy.system.slider :as slider]))


;; sample 

(def ioframe-config2
  #:ioframe {:type :mini
             :name "mini-1"
             :db-url "/temp/frame/solar-1.fra"
             :description "只有太阳和地球的小型系统"})


#_(def ioframe-config-sample
  #:ioframe {:db db
             :name "slider1-1"
             :type :slider
             :description "一个进度条"})


;; schema

(def schema {:ioframe/name {:db/unique :db.unique/identity}})


;; transform

(defmulti create-ioframe-system (fn [ioframe] (:ioframe/type ioframe)))

(defmethod create-ioframe-system :mini
  [ioframe]
  (mini/create-ioframe-system ioframe))

(defmethod create-ioframe-system :slider
  [ioframe]
  (slider/create-ioframe-system ioframe))


;; find

(def all-names-query
  '[:find [?name ...]
    :where [?id :ioframe/name ?name]])

(def all-id-and-names-query
  '[:find ?id ?name
    :where [?id :ioframe/name ?name]])

(defn find-all-names [db]
  (d/q all-names-query db))


(comment 
  
  (create-ioframe-system ioframe-config2)
  
  )