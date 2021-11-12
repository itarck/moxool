(ns astronomy.conn.mini-factory
  (:require
   [datascript.core :as d]
   [astronomy.conn.core :refer [create-empty-conn! kick-start!] :as conn.core]
   [astronomy.data.basic :as d.basic]
   [astronomy.data.celestial :as d.celestial]
   [astronomy.data.coordinate :as d.coordinate]
   [astronomy.data.tool :as d.tool]
   [astronomy.data.misc :as d.misc]
   [astronomy.lib.api :as api]))


;; processes

(def tools1
  [{:db/id [:tool/name "ppt tool"]}
   {:db/id [:tool/name "clock control 1"]}
   {:db/id [:tool/name "spaceship camera tool"]}
   {:db/id [:tool/name "astronomical-coordinate-tool"]}])


(defn create-db1 []
  (let [conn (create-empty-conn!)]
    (d/transact! conn d.basic/dataset1)
    (d/transact! conn d.celestial/dataset1)
    (d/transact! conn d.celestial/dataset3)
    (d/transact! conn [(assoc d.coordinate/astronomical-coordinate-1
                              :astro-scene/_coordinate [:scene/name "solar"])])
    (d/transact! conn d.tool/dataset1)
    (d/transact! conn d.misc/dataset1)

    (kick-start! conn tools1)
    @conn))


(def tools2
  [{:db/id [:tool/name "ppt tool"]}
   {:db/id [:tool/name "clock control 1"]}
   {:db/id [:tool/name "spaceship camera tool"]}
   {:db/id [:tool/name "horizon-coordinate-tool"]}
   {:db/id [:tool/name "terrestrial-coordinate-tool"]}
   {:db/id [:tool/name "constellation-tool"]}
   {:db/id [:tool/name "atmosphere-tool"]}
   {:db/id [:tool/name "astronomical-coordinate-tool"]}
   {:db/id [:tool/name "ellipse-orbit-tool"]}])

(defn create-db2 []
  (let [conn (create-empty-conn!)]
    (d/transact! conn d.basic/dataset1)
    (d/transact! conn d.celestial/dataset1)
    (d/transact! conn d.celestial/dataset2)
    (d/transact! conn d.celestial/dataset3)
    (d/transact! conn d.coordinate/dataset1)
    (d/transact! conn d.tool/dataset1)
    (d/transact! conn d.misc/dataset1)

    (kick-start! conn tools2)
    @conn))


(comment

  (->
   (create-db1)
   (api/save-db-file "/temp/frame/solar-2.fra"))

  )

