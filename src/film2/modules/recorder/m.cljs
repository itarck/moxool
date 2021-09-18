(ns film2.modules.recorder.m
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]))


(def sample
  #:recorder {:db/id -2
              :name "default"
              :current-iovideo -202})


(def schema {:recorder/name {:db/unique :db.unique/identity}
             :recorder/current-iovideo {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})
