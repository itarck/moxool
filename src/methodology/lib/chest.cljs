(ns methodology.lib.chest
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]))


;; 封装数据库操作

(defn create-conn!
  ([]
   (let [conn (d/create-conn)]
     (p/posh! conn)
     conn))
  ([schema]
   (let [conn (d/create-conn schema)]
     (p/posh! conn)
     conn))
  ([schema initial-tx]
   (let [conn (d/create-conn schema)]
     (p/posh! conn)
     (p/transact! conn initial-tx)
     conn)))


(defn pull-one [db id]
  (d/pull db '[*] id))


(defn pull-many [db ids]
  (d/pull-many db '[*] ids))


(defn find-ids-by-attr [db attr]
  (d/q '[:find [?id ...]
         :in $ ?attr
         :where
         [?id ?attr]]
       db attr))


(defn find-ids-by-attr-value [db attr value]
  (d/q '[:find [?id ...]
         :in $ ?attr ?value
         :where
         [?id ?attr ?value]]
       db attr value))

(defn posh-one [conn entity]
  @(p/pull conn '[*] (:db/id entity)))


(defn sub-one [conn id]
  @(p/pull conn '[*] id))


(defn sub-many [conn ids]
  (doall (mapv (fn [id] @(p/pull conn '[*] id)) ids)))

