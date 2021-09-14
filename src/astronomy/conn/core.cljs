(ns astronomy.conn.core
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [methodology.model.core :as mtd-model]
   [astronomy.model.core :as ast-model]

   [methodology.model.user.backpack :as m.backpack]
   [astronomy.objects.astro-scene.m :as m.astro-scene]
   [astronomy.objects.clock.m :as m.clock]))


(def schema (merge ast-model/schema
                   mtd-model/schema))


(defn create-empty-conn! []
  (let [conn (d/create-conn schema)]
    (p/posh! conn)
    conn))


(defn kick-start! [conn tools]
  (let [clock-id [:clock/name "default"]
        astro-scene (d/pull @conn '[*] [:scene/name "solar"])
        person (d/pull @conn '[*] [:person/name "dr who"])
        backpack (d/pull @conn '[*] (-> person :person/backpack :db/id))]
    (p/transact! conn (m.clock/set-clock-time-in-days-tx clock-id 0))
    (p/transact! conn (m.astro-scene/refresh-tx @conn astro-scene))
    (p/transact! conn (m.backpack/put-in-backpack-tx backpack tools))))


(comment 
  
  )