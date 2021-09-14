(ns astronomy.ig.conn
  (:require
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [cljs.core.async :refer [go >! <!]]
   [cljs-http.client :as http]
   [integrant.core :as ig]

   [methodology.model.core :as mtd-model]

   [astronomy.model.user.universe-tool :as m.universe-tool]
   [astronomy.model.user.clock-tool :as m.clock-tool]
   [astronomy.model.user.info-tool :as m.info-tool]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   [astronomy.tools.ppt-tool.m :as m.ppt-tool]
   [astronomy.model.user.ruler-tool :as m.ruler-tool]

   [astronomy.objects.astro-scene.m :as m.astro-scene]
   [astronomy.objects.atmosphere.m :as m.atmosphere]
   [astronomy.objects.astronomical-point.m :as m.astronomical-point]
   [astronomy.objects.clock.m :as m.clock]
   [astronomy.objects.celestial.m :as m.celestial]
   [astronomy.objects.coordinate.m :as m.coordinate]
   [astronomy.objects.constellation.m :as m.constellation]
   [astronomy.objects.circle-orbit.m :as circle-orbit.m]
   [astronomy.objects.galaxy.m :as m.galaxy]
   [astronomy.objects.star.m :as m.star]
   [astronomy.objects.planet.m :as planet.m]
   [astronomy.objects.satellite.m :as satellite.m]
   [astronomy.objects.spin.m :as m.spin]
   [astronomy.objects.horizon-coordinate.m :as horizon-coordinate.m]
   [astronomy.objects.astronomical-coordinate.m :as astronomical-coordinate.m]
   [astronomy.objects.terrestrial-coordinate.m :as terrestrial-coordinate.m]
   
   ))


(def schema
  (merge
   mtd-model/schema

   m.astro-scene/schema
   m.celestial/schema
   circle-orbit.m/schema
   m.clock/schema
   m.galaxy/schema
   planet.m/schema
   satellite.m/schema
   m.spin/schema
   m.star/schema
   m.constellation/schema
   m.atmosphere/schema
   m.coordinate/schema
   astronomical-coordinate.m/schema
   terrestrial-coordinate.m/schema
   horizon-coordinate.m/schema
   m.astronomical-point/schema

   m.universe-tool/schema
   m.clock-tool/schema
   m.spaceship/schema
   m.info-tool/schema
   m.ppt-tool/schema
   m.ruler-tool/schema))


(defmethod ig/init-key :astronomy/conn [_k config]
  (println "astronomy/conn start: " (js/Date))
  (let [{:conn/keys [db-url initial-db]} config
        conn (d/create-conn schema)]
    (when initial-db
      (d/reset-conn! conn initial-db))
    (when db-url
      (go (let [response (<! (http/get db-url))
                stored-data (:body response)
                stored-db (when stored-data (dt/read-transit-str stored-data))]
            (d/reset-conn! conn stored-db))))
    (p/posh! conn)
    (println "astronomy/conn end: " (js/Date))
    conn))