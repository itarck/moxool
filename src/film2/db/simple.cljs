(ns film2.db.simple
  (:require
   [datascript.core :as d]
   [datascript.transit :as dt]
   [film2.data.studio :as d.studio]
   [film2.parts.schema :refer [schema]]))


(defn create-empty-studio-db []
  (let [db (d/empty-db schema)]
    db))

(defn create-studio-db-1 []
  (let [db (create-empty-studio-db)]
    (d/db-with db d.studio/dataset)))


(comment 
  
  (def db (create-studio-db-1))

  (= (dt/read-transit-str (dt/write-transit-str db)) db)

  )