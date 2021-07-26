(ns astronomy.scripts.init-conn2
  (:require
   [cljs.core.async :refer [go >! <! chan]]
   [cljs-http.client :as http]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [methodology.lib.client :as client]
   [methodology.model.user.backpack :as m.backpack]
   [methodology.model.core :as mtd-model]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.core :as ast-model]
   [astronomy.model.clock :as m.clock]

   [astronomy.data.basic :as d.basic]
   [astronomy.data.celestial :as d.celestial]
   [astronomy.data.galaxy :as d.galaxy]
   [astronomy.data.coordinate :as d.coordinate]
   [astronomy.data.tool :as d.tool]
   [astronomy.data.stars :as d.stars]
   [astronomy.data.constellation :as d.constel]
   ))


(def schema (merge ast-model/schema
                   mtd-model/schema))



;; processes


(def tools
  [{:db/id [:tool/name "ppt tool"]}
   {:db/id [:tool/name "clock control 1"]}
   {:db/id [:tool/name "goto celestial tool"]}
   {:db/id [:tool/name "spaceship camera tool"]}
   {:db/id [:tool/name "horizon-coordinate-tool"]}
   {:db/id [:tool/name "terrestrial-coordinate-tool"]}
   {:db/id [:tool/name "constellation-tool"]}
   {:db/id [:tool/name "universe tool"]}
   {:db/id [:tool/name "atmosphere-tool"]}
   {:db/id [:tool/name "eagle-eye-tool"]}
   {:db/id [:tool/name "astronomical-coordinate-tool"]}])


(defn kick-start! [conn]
  (let [clock-id [:clock/name "default"]
        astro-scene (d/pull @conn '[*] [:scene/name "solar"])
        person (d/pull @conn '[*] [:person/name "dr who"])
        backpack (d/pull @conn '[*] (-> person :person/backpack :db/id))]
    (p/transact! conn (m.clock/set-clock-time-in-days-tx clock-id 0))
    (p/transact! conn (m.astro-scene/refresh-tx @conn astro-scene))
    (p/transact! conn (m.backpack/put-in-backpack-tx backpack tools))))


(defn init-conn! []
  (let [conn (d/create-conn schema)]
    (d/transact! conn d.basic/dataset1)
    (d/transact! conn d.celestial/dataset1)
    ;; (d/transact! conn d.celestial/dataset2)
    (d/transact! conn d.celestial/dataset3)
    (d/transact! conn d.galaxy/dataset1)
    (d/transact! conn d.coordinate/dataset1)

    (p/transact! conn d.stars/dataset1)
    (d/transact! conn d.constel/dataset1)
    (d/transact! conn d.constel/dataset2)
    (d/transact! conn d.constel/dataset3)
    (d/transact! conn d.tool/dataset1)

    (kick-start! conn)
    conn))

(defn async-run! []
  (let [ch (chan)
        conn (init-conn!)]
    (println "async-run init-conn !!!!")
    (go
      (let [db-name "/temp/free-mode.edn"
            response (<! (http/post "/api/db/save" {:edn-params {:db-name db-name
                                                                 :db-value (dt/write-transit-str @conn)}}))]
        (println (:body response)))
      (>! ch @conn))
    ch))

(defn async-run2! []
  (let [conn (init-conn!)]
    (println "async-run init-conn !!!!")
    (client/save-db-file @conn "/temp/free-mode.edn")))


(comment

  (async-run!)
;;   
  )

