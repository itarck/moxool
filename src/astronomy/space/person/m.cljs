(ns astronomy.space.person.m
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]))


(def person1 #:person {:db/id -1
                       :name "dr who"
                       :backpack {:name "default"
                                  :owner [:person/name "dr who"]}
                       :right-tool {:db/id -2}
                       :entity/type :person})


(def schema
  {:person/name {:db/unique :db.unique/identity}
   :person/backpack {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :person/mouse {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :person/camera-control {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :person/right-tool {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

;; transform

(defn in-right-hand? [user tool]
  (= (get-in user [:person/right-tool :db/id])
     (:db/id tool)))

;; find and pull

(defn pull2 [db id]
  (d/pull db '[* {:person/right-tool [*]
                  :person/backpack [*]}] id))

;; tx

(defn select-tool-tx [person tool-id]
  (when tool-id
    [[:db/add (:db/id person) :person/right-tool tool-id]]))


(defn drop-tool-tx [person]
  [[:db.fn/retractAttribute (:db/id person) :person/right-tool]])

;; sub

(defn sub-user-name-exist? [conn user-name]
  (seq
   @(p/q '[:find [?id ...]
           :in $ ?user-name
           :where
           [?id :person/name ?user-name]]
         conn user-name)))

