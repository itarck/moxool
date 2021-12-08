(ns astronomy.space.backpack.m
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]))


(def sample
  #:backpack {:db/id -3
              :name "default"
              :owner -1
              :cell [#:backpack-cell{:index 0
                                     :tool -1}
                     #:backpack-cell{:index 1
                                     :tool -2}]})


(def schema
  {:backpack/name {:db/unique :db.unique/identity}
   :backpack/owner {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :backpack/active-cell {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :backpack/cell {:db/valueType :db.type/ref
                   :db/cardinality :db.cardinality/many
                   :db/isComponent true}
   :backpack-cell/tool {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


(defn find-nth-cell [backpack nth-cell]
  (->
   (filter (fn [cell] (= (:backpack-cell/index cell) nth-cell))
           (:backpack/cell backpack))
   first))

(defn put-in-cell-tx [backpack nth-cell tool]
  (let [cell (find-nth-cell backpack nth-cell)]
    [[:db/add (:db/id cell) :backpack-cell/tool (:db/id tool)]]))

(defn active-cell-tx [backpack cell-id]
  [[:db/add (:db/id backpack) :backpack/active-cell cell-id]])

(defn deactive-cell-tx [backpack]
  [[:db.fn/retractAttribute (:db/id backpack) :backpack/active-cell]])

(defn put-in-backpack-tx [backpack tools]
  (apply concat
         (for [i (range (count tools))]
           (put-in-cell-tx backpack i (get tools i)))))

(defn clear-backpack-tx [db backpack]
  (let [backpack-1 (d/pull db '[{:backpack/cell [*]}] (:db/id backpack))
        tx (vec (for [cell (:backpack/cell backpack-1)]
                  [:db.fn/retractAttribute (:db/id cell) :backpack-cell/tool]))]
    tx))

;; subscribe 

(defn sub-backpack [conn id]
  @(p/pull conn '[*] id))

(defn sub-backpack-fully [conn id]
  @(p/pull conn '[* {:backpack/cell [{:backpack-cell/tool [*]}]}] id))

