(ns film2.test-ioframe
  (:require
   [datascript.core :as d]
   [film2.parts.schema :refer [schema]]
   [film2.data.studio :as d.studio]
   [film2.modules.ioframe.m :as ioframe.m]))


(def test-db
  (let [conn (d/create-conn schema)]
    (d/transact! conn d.studio/dataset)
    @conn))


test-db


(ioframe.m/find-all-names test-db)