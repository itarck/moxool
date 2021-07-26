(ns astronomy.conn.core
  (:require
   [datascript.core :as d]
   [methodology.model.core :as mtd-model]
   [astronomy.model.core :as ast-model]))


(def schema (merge ast-model/schema
                   mtd-model/schema))


(defn create-empty-conn! []
  (d/create-conn schema))
