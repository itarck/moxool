(ns astronomy.scripts.angel.lib
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.space.backpack.m :as backpack.m]))


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

