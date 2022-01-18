(ns laboratory.dbs.helper
  (:require
   [datascript.core :as d]
   [laboratory.system :as sys]))

(defn create-schema-unit []
  (let [sys (sys/init {} [::sys/schema])]
    (::sys/schema sys)))


(defn create-initial-db
  [initial-tx]
  (let [schema (create-schema-unit)
        conn (d/create-conn schema)]
    (d/transact! conn initial-tx)
    @conn))

