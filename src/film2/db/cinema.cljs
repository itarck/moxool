(ns film2.db.cinema
  (:require
   [datascript.core :as d]
   [film2.data.ioframe :as d.ioframe]
   [film2.data.cinema :as d.cinema]
   [film2.parts.schema :refer [schema]]))


(def simple-db
  (let [db (d/empty-db schema)
        dataset [d.ioframe/mini-1
                 d.ioframe/mini-2
                 d.ioframe/solar-planet5
                 d.ioframe/scene-1-1
                 d.ioframe/scene-1-2
                 d.ioframe/scene-baseline
                 d.cinema/default]]
    (d/db-with db dataset)))

