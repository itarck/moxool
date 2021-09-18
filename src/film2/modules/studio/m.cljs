(ns film2.modules.studio.m
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]))


(def sample
  #:studio {:db/id -2
            :name "default"})

(def schema {:studio/name {:db/unique :db.unique/identity}
             :studio/player {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}
             :studio/editor {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}
             :studio/recorder {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})
