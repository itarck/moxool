(ns astronomy.conn.core
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [methodology.model.core :as mtd-model]
   [astronomy.model.core :as ast-model]
   [astronomy.data.basic :as d.basic]))


(def schema (merge ast-model/schema
                   mtd-model/schema))


(defn create-empty-conn! []
  (let [conn (d/create-conn schema)]
    (p/posh! conn)
    conn))


(defn create-basic-conn! []
  (let [conn (create-empty-conn!)]
    (p/transact! conn d.basic/dataset1)
    conn))


(comment 
  
  )