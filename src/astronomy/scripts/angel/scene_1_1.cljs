(ns astronomy.scripts.angel.scene-1-1
  (:require
   [datascript.core :as d]
   [astronomy.conn.core :refer [create-empty-conn! kick-start!] :as conn.core]
   [astronomy.data.basic :as d.basic]
   [astronomy.data.celestial :as d.celestial]
   [astronomy.data.coordinate :as d.coordinate]
   [astronomy.data.tool :as d.tool]
   [astronomy.data.misc :as d.misc]
   [astronomy.lib.api :as api]))


;; for mini2

(def tools2
  [{:db/id [:tool/name "ppt tool"]}
   {:db/id [:tool/name "clock control 1"]}
   {:db/id [:tool/name "spaceship camera tool"]}
   {:db/id [:tool/name "horizon-coordinate-tool"]}
   {:db/id [:tool/name "terrestrial-coordinate-tool"]}
   {:db/id [:tool/name "astronomical-coordinate-tool"]}
   {:db/id [:tool/name "planet-tool"]}
   {:db/id [:tool/name "constellation-tool"]}
   {:db/id [:tool/name "atmosphere-tool"]}
   {:db/id [:tool/name "ellipse-orbit-tool"]}])


(def basic-dataset
  [d.basic/camera
   d.basic/clock
   d.basic/scene
   (assoc d.basic/spaceship-camera-control
          :spaceship-camera-control/position [40000000 40000000 40000000])
   d.basic/user1])

(defn create-db []
  (let [conn (create-empty-conn!)]
    (d/transact! conn basic-dataset)
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
   (create-db)
   (api/save-db-file "/private/frame/dev-20211116-1.fra"))
  ;; 
  )

