(ns astronomy.scripts.angel.scene-1-1
  (:require
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [astronomy.lib.api :as api]
   [astronomy.space.backpack.m :as m.backpack]
   [astronomy.conn.core :refer [create-empty-conn!] :as conn.core]
   [astronomy.tools.spaceship-camera-control.m :as m.spaceship]
   [astronomy.objects.astro-scene.m :as m.astro-scene]
   [astronomy.objects.clock.m :as m.clock]
   [astronomy.objects.celestial.m :as celestial.m])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))


(def baseline-db
  (->>
   (read-resource "frame/dev/base-1.fra")
   (dt/read-transit-str)))


(defn init-scene! [conn]
  (let [objects [{:db/id [:star/name "sun"]}
                 {:db/id [:planet/name "earth"]}]
        astro-scene (d/pull @conn '[*] [:scene/name "solar"])
        tx1 (m.astro-scene/put-objects-tx astro-scene objects)
        tx2 (mapcat #(celestial.m/add-clock-tx % {:db/id [:clock/name "default"]}) objects)]
    (p/transact! conn (concat tx1 tx2))))


(defn init-tool! [conn]
  (let [tools [{:db/id [:tool/name "clock control 1"]}
               {:db/id [:tool/name "planet-tool"]}]
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
    (d/reset-conn! conn baseline-db)
    (init-scene! conn)
    (init-tool! conn)
    (kick-start! conn)
    @conn))


(comment

  (->
   (create-db)
   (api/save-db-file "/frame/dev/dev-20211206-1.fra"))

  
  (time
   (let [db (->>
             (read-resource "frame/dev/base-1.fra")
             (dt/read-transit-str))]
     :done))

  (time
   (let [_ (create-db)]
     :done))
  
  ;; 

  )


