(ns astronomy.conn.core
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [methodology.model.core :as mtd-model]
   [astronomy.model.core :as ast-model]
   [astronomy.data.basic :as d.basic]
   [astronomy.data.celestial :as d.celestial]
   [astronomy.data.galaxy :as d.galaxy]
   [astronomy.data.coordinate :as d.coordinate]
   [astronomy.data.tool :as d.tool]
   [astronomy.data.stars :as d.stars]
   [astronomy.data.constellation :as d.constel]))


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


(defn create-narrow-conn-1!
  "具有一部分基础的数据，包括基础、星体、坐标、工具等"
  []
  (let [conn (create-empty-conn!)]
    (p/transact! conn d.basic/dataset1)
    (p/transact! conn d.celestial/dataset1)
    (p/transact! conn d.celestial/dataset3)
    (p/transact! conn d.coordinate/dataset1)
    (p/transact! conn d.tool/dataset1)
    conn))


(comment 
  
  )