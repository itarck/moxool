(ns film2.modules.ioframe.m
  (:require
   [datascript.core :as d]
   [astronomy.system.solar :as solar]
   [astronomy.system.slider :as slider]
   [astronomy.system.city :as city]))


;; sample 

(def ioframe-config2
  #:ioframe {:type :mini
             :name "mini-1"
             :db-transit-str ""
             :description "只有太阳和地球的小型系统"})


;; schema

(def schema {:ioframe/name {:db/unique :db.unique/identity}})



;; transform

(defmulti create-ioframe-system (fn [ioframe] (:ioframe/type ioframe)))

(defmethod create-ioframe-system :solar
  [ioframe]
  (let [{:ioframe/keys [db db-url db-transit-str]} ioframe
        conn-config (cond
                      db-transit-str {:db-transit-str db-transit-str}
                      db-url {:db-url db-url}
                      db {:initial-db db})
        user-config #:astronomy {:conn conn-config}
        astronomy-instance (solar/create-system! user-config)]
    #:ioframe-system {:view (:astronomy/root-view astronomy-instance)
                      :conn (:astronomy/conn astronomy-instance)
                      :dom-atom (:astronomy/dom-atom astronomy-instance)
                      :meta-atom (:astronomy/meta-atom astronomy-instance)
                      :service-chan (:astronomy/service-chan astronomy-instance)
                      :ig-instance astronomy-instance}))

(defmethod create-ioframe-system :slider
  [ioframe]
  (slider/create-ioframe-system ioframe))

(defmethod create-ioframe-system :city
  [ioframe]
  (city/create-ioframe-system ioframe))

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