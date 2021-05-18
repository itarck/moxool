(ns methodology.model.user.person
  (:require 
   [datascript.core :as d]))


(def person1 #:person {:db/id -1
                       :name "dr who"
                       :backpack {:name "default"
                                  :owner [:person/name "dr who"]}
                       :right-tool {:db/id -2}
                       :entity/type :person})


(def schema
  {:person/name {:db/unique :db.unique/identity}
   :person/backpack {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :person/right-tool {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


(defn pull2 [db id]
  (d/pull db '[* {:person/right-tool [*]
                  :person/backpack [*]}] id))

