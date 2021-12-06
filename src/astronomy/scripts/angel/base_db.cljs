(ns astronomy.scripts.angel.base-db
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.lib.api :as api]
   [astronomy.space.backpack.m :as m.backpack]
   [astronomy.conn.core :refer [create-empty-conn!]]
   [astronomy.tools.spaceship-camera-control.m :as m.spaceship]
   [astronomy.objects.astro-scene.m :as m.astro-scene]
   [astronomy.objects.clock.m :as m.clock]
   [astronomy.objects.celestial.m :as celestial.m]
   [astronomy.data.basic :as d.basic]
   [astronomy.data.celestial :as d.celestial]
   [astronomy.data.galaxy :as d.galaxy]
   [astronomy.data.coordinate :as d.coordinate]
   [astronomy.data.tool :as d.tool]
   [astronomy.data.stars :as d.stars]
   [astronomy.data.constellation :as d.constel]
   [astronomy.data.misc :as d.misc]))

;; processes

(defn load-datasets! [conn]
  (d/transact! conn d.basic/dataset1)
  (d/transact! conn d.celestial/dataset1)
  (d/transact! conn d.celestial/dataset2)
  (d/transact! conn d.celestial/dataset3)
  (d/transact! conn d.galaxy/dataset1)
  (d/transact! conn d.coordinate/dataset1)
  (d/transact! conn d.coordinate/dataset2)
  (d/transact! conn d.stars/dataset1)
  (d/transact! conn d.constel/dataset1)
  (d/transact! conn d.constel/dataset2)
  (d/transact! conn d.constel/dataset3)
  (d/transact! conn d.tool/dataset1)
  (d/transact! conn d.misc/dataset1))


(defn init-scene! [conn]
  (let [objects [{:db/id [:star/name "sun"]}
                 {:db/id [:planet/name "earth"]}]
        astro-scene (d/pull @conn '[*] [:scene/name "solar"])
        tx1 (m.astro-scene/put-objects-tx astro-scene objects)
        tx2 (mapcat #(celestial.m/add-clock-tx % {:db/id [:clock/name "default"]}) objects)]
    (p/transact! conn (concat tx1 tx2))))


(defn init-tool! [conn]
  (let [tools [{:db/id [:tool/name "ppt tool"]}
               {:db/id [:tool/name "clock control 1"]}
               {:db/id [:tool/name "planet-tool"]}
               {:db/id [:tool/name "satellite-tool"]}
               {:db/id [:tool/name "spaceship camera tool"]}
               {:db/id [:tool/name "horizon-coordinate-tool"]}
               {:db/id [:tool/name "terrestrial-coordinate-tool"]}
               {:db/id [:tool/name "astronomical-coordinate-tool"]}
               #_{:db/id [:tool/name "constellation-tool"]}
               #_{:db/id [:tool/name "universe tool"]}
               {:db/id [:tool/name "atmosphere-tool"]}
               {:db/id [:tool/name "astronomical-point-tool"]}
               #_{:db/id [:tool/name "ruler-tool"]}
               {:db/id [:tool/name "ellipse-orbit-tool"]}]
        person (d/pull @conn '[*] [:user/name "dr who"])
        backpack (d/pull @conn '[*] (-> person :user/backpack :db/id))]
    (p/transact! conn (m.backpack/put-in-backpack-tx backpack tools))))


(defn kick-start! [conn]
  (let [clock-id [:clock/name "default"]
        astro-scene (d/pull @conn '[*] [:scene/name "solar"])
        scc (d/pull @conn '[*] [:spaceship-camera-control/name "default"])
        coordinate {:db/id [:coordinate/name "地球坐标系"]}]
    (p/transact! conn (m.clock/set-clock-time-in-days-tx clock-id 0))
    (p/transact! conn (m.astro-scene/refresh-tx @conn astro-scene))
    (p/transact! conn (m.spaceship/update-min-distance-tx @conn scc coordinate))))


(defn create-db []
  (let [conn (create-empty-conn!)]
    (load-datasets! conn)
    ;; (init-scene! conn)
    ;; (init-tool! conn)
    ;; (kick-start! conn)
    @conn))


(def base-db
  (create-db))


(comment

  (def path
    "/private/frame/temp/dev-20211206-7.fra")
  
  (def path2 
    "/frame/dev/base-1.fra")

  
  (api/save-db-file (create-db) path2)
  

  (let [db (create-db)]
    (d/pull db '[* {:object/_scene [*]}] [:scene/name "solar"]))

  
  (time (create-db))
;;   
  )

