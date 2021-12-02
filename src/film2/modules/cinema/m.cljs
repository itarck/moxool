(ns film2.modules.cinema.m
  (:require
   [datascript.core :as d]))


(def sample
  #:cinema {:db/id -2
            :name "default"
            :ioframe-names ["a" "b" "c"]})

(def schema
  #:cinema{:name {:db/unique :db.unique/identity}
           :editor {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}
           :current-ioframe {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})


(defn pull-all
  [db cinema-id]
  (d/pull db '[*] cinema-id))


(defn change-ioframe-tx
  [db cinema ioframe-name]
  [#:cinema {:db/id (:db/id cinema)
             :current-ioframe-name ioframe-name}])
