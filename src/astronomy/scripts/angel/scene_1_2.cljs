(ns astronomy.scripts.angel.scene-1-2
  (:require
   [datascript.core :as d]
   [astronomy.conn.core :refer [create-empty-conn! kick-start!] :as conn.core]
   [astronomy.data.basic :as d.basic]
   [astronomy.data.celestial :as d.celestial]
   [astronomy.data.coordinate :as d.coordinate]
   [astronomy.data.tool :as d.tool]
   [astronomy.data.galaxy :as d.galaxy]
   [astronomy.data.constellation :as d.constel]
   [astronomy.data.stars :as d.stars]
   [astronomy.lib.api :as api]))


;; for mini2

(def tools2
  [{:db/id [:tool/name "clock control 1"]}
   {:db/id [:tool/name "planet-tool"]}
   {:db/id [:tool/name "constellation-tool"]}])


(def basic-dataset
  [d.basic/camera
   d.basic/clock
   d.basic/scene
   d.basic/spaceship-camera-control
   d.basic/user1
   d.celestial/sun
   d.celestial/earth
   d.coordinate/astronomical-coordinate-earth-center
   d.galaxy/galaxy])


(def tools 
  [d.tool/planet-tool-1
   d.tool/clock-tool1
   d.tool/constellation-tool-1])


(defn create-db []
  (let [conn (create-empty-conn!)]
    (d/transact! conn basic-dataset)
    (d/transact! conn tools)
    (d/transact! conn d.constel/dataset1)
    (d/transact! conn d.constel/dataset2)
    (d/transact! conn d.constel/dataset3)
    (d/transact! conn d.stars/stars-data)

    (kick-start! conn tools2)
    @conn))


(comment

  (->
   (create-db)
   (api/save-db-file "/private/frame/angel/scene-1-2-v1.fra"))
  ;; 
  )

