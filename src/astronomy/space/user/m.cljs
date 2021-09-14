(ns astronomy.space.user.m
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]))


(def user1 #:user {:db/id -1
                   :name "dr who"
                   :backpack {:name "default"
                              :owner [:user/name "dr who"]}
                   :right-tool {:db/id -2}
                   :entity/type :user})


(def schema
  {:user/name {:db/unique :db.unique/identity}
   :user/backpack {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/mouse {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/camera-control {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/right-tool {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

;; transform

(defn in-right-hand? [user tool]
  (= (get-in user [:user/right-tool :db/id])
     (:db/id tool)))

;; find and pull

(defn pull2 [db id]
  (d/pull db '[* {:user/right-tool [*]
                  :user/backpack [*]}] id))

;; tx

(defn select-tool-tx [user tool-id]
  (when tool-id
    [[:db/add (:db/id user) :user/right-tool tool-id]]))


(defn drop-tool-tx [user]
  [[:db.fn/retractAttribute (:db/id user) :user/right-tool]])

;; sub

(defn sub-user-name-exist? [conn user-name]
  (seq
   @(p/q '[:find [?id ...]
           :in $ ?user-name
           :where
           [?id :user/name ?user-name]]
         conn user-name)))

