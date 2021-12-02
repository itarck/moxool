(ns film2.db.studio
  (:require
   [datascript.core :as d]
   [film2.data.studio :as d.studio]
   [film2.data.ioframe :as d.ioframe]
   [film2.data.iovideo :as d.iovideo]
   [film2.parts.schema :refer [schema]]))


(def simple-db
  (let [db (d/empty-db schema)
        dataset [d.ioframe/mini-1
                 d.ioframe/mini-2
                 d.iovideo/mini
                 d.studio/default]]
    (d/db-with db dataset)))

