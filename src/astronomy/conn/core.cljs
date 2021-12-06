(ns astronomy.conn.core
  (:require
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [astronomy.conn.schema :refer [schema]]
   [astronomy.space.backpack.m :as m.backpack]
   [astronomy.objects.astro-scene.m :as m.astro-scene]
   [astronomy.objects.clock.m :as m.clock]
   [astronomy.tools.spaceship-camera-control.m :as m.spaceship]))



(defn create-empty-conn! []
  (let [conn (d/create-conn schema)]
    (p/posh! conn)
    conn))


(defn create-conn-from-db [db]
  (let [conn (d/create-conn schema)]
    (d/reset-conn! conn db)
    (p/posh! conn)
    conn))


(defn kick-start! [conn tools]
  (let [clock-id [:clock/name "default"]
        astro-scene (d/pull @conn '[*] [:scene/name "solar"])
        person (d/pull @conn '[*] [:user/name "dr who"])
        backpack (d/pull @conn '[*] (-> person :user/backpack :db/id))]
    (p/transact! conn (m.clock/set-clock-time-in-days-tx clock-id 0))
    (p/transact! conn (m.astro-scene/refresh-tx @conn astro-scene))
    (p/transact! conn (m.backpack/put-in-backpack-tx backpack tools))
    (p/transact! conn (m.spaceship/update-min-distance-tx @conn
                                                          (:user/camera-control person)
                                                          (:astro-scene/coordinate astro-scene)))
    (p/transact! conn (m.spaceship/update-default-position-tx @conn (:user/camera-control person)))))


(comment 
  
  )