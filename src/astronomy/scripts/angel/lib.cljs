(ns astronomy.scripts.angel.lib
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.space.backpack.m :as backpack.m]
   [astronomy.objects.astro-scene.m :as astro-scene.m]
   [astronomy.objects.celestial.m :as celestial.m]))


(defn clear-backpack! [conn]
  (let [person (d/pull @conn '[*] [:user/name "dr who"])
        backpack (d/pull @conn '[*] (-> person :user/backpack :db/id))
        tx (backpack.m/clear-backpack-tx @conn backpack)]
    (p/transact! conn tx)))


(defn init-tool! [conn tools]
  (let [person (d/pull @conn '[*] [:user/name "dr who"])
        backpack (d/pull @conn '[*] (-> person :user/backpack :db/id))]
    (clear-backpack! conn)
    (p/transact! conn (backpack.m/put-in-backpack-tx backpack tools))))


(defn init-scene! [conn objects]
  (let [astro-scene (d/pull @conn '[*] [:scene/name "solar"])
        tx1 (astro-scene.m/put-objects-tx astro-scene objects)
        tx2 (mapcat #(celestial.m/add-clock-tx % {:db/id [:clock/name "default"]}) objects)]
    (p/transact! conn (concat tx1 tx2))))


(def all-tools 
  [{:db/id [:tool/name "ppt tool"]}
   {:db/id [:tool/name "clock control 1"]}
   {:db/id [:tool/name "planet-tool"]}
   {:db/id [:tool/name "satellite-tool"]}
   {:db/id [:tool/name "spaceship camera tool"]}
   {:db/id [:tool/name "horizon-coordinate-tool"]}
   {:db/id [:tool/name "constellation-tool"]}
   {:db/id [:tool/name "terrestrial-coordinate-tool"]}
   {:db/id [:tool/name "astronomical-coordinate-tool"]}
   {:db/id [:tool/name "atmosphere-tool"]}
   {:db/id [:tool/name "astronomical-point-tool"]}
   {:db/id [:tool/name "ellipse-orbit-tool"]}])

