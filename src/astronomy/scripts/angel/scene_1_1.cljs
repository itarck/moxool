(ns astronomy.scripts.angel.scene-1-1
  (:require
   [datascript.core :as d]
   [astronomy.conn.core :refer [create-empty-conn! kick-start!] :as conn.core]
   [astronomy.data.basic :as d.basic]
   [astronomy.data.celestial :as d.celestial]
   [astronomy.data.coordinate :as d.coordinate]
   [astronomy.data.tool :as d.tool]
   [astronomy.data.galaxy :as d.galaxy]
   [astronomy.lib.api :as api]))


;; for mini2

(def tools2
  [{:db/id [:tool/name "clock control 1"]}
   {:db/id [:tool/name "planet-tool"]}])


(def basic-dataset
  [d.basic/camera
   d.basic/clock
   d.basic/scene
   d.basic/spaceship-camera-control
   d.basic/user1
   d.celestial/sun
   d.celestial/earth
   d.coordinate/astronomical-coordinate-earth-center
  ;;  d.tool/astronomical-coordinate-tool
   d.tool/planet-tool-1
   d.tool/clock-tool1
   d.galaxy/galaxy
   ])


(defn create-db []
  (let [conn (create-empty-conn!)]
    (d/transact! conn basic-dataset)
    ;; (d/transact! conn d.stars/dataset1)

    (kick-start! conn tools2)
    @conn))


(comment

  (->
   (create-db)
   (api/save-db-file "/private/frame/dev-20211116-1.fra"))
  ;; 
  )

