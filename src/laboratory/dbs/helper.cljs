(ns laboratory.dbs.helper
  (:require
   [datascript.core :as d]
   [laboratory.system.zero :as zero]))

(defn create-schema-unit []
  (let [sys (zero/init {} [::zero/schema])]
    (::zero/schema sys)))


(defn create-initial-db
  [initial-tx]
  (let [schema (create-schema-unit)
        conn (d/create-conn schema)]
    (d/transact! conn initial-tx)
    @conn))

