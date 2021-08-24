(ns astronomy.model.core
  (:require
   [datascript.core :as d]
   [methodology.model.core :as mtd-model]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.model.circle-orbit :as m.circle-orbit]
   [astronomy.model.clock :as m.clock]
   [astronomy.model.galaxy :as m.galaxy]
   [astronomy.model.spin :as m.spin]
   [astronomy.model.star :as m.star]
   [astronomy.model.constellation :as m.constellation]
   [astronomy.model.atmosphere :as m.atmosphere]
   [astronomy.model.coordinate :as m.coordinate]
   
   [astronomy.model.astronomical-point :as m.astronomical-point]

   [astronomy.model.user.universe-tool :as m.universe-tool]
   [astronomy.model.user.clock-tool :as m.clock-tool]
   [astronomy.model.user.info-tool :as m.info-tool]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   [astronomy.model.user.goto-celestial-tool :as m.goto-tool]
   [astronomy.model.user.ppt-tool :as m.ppt-tool]
   [astronomy.model.user.ruler-tool :as m.ruler-tool]

   [astronomy.objects.planet.m :as planet.m]
   [astronomy.objects.satellite.m :as satellite.m]
   [astronomy.objects.horizon-coordinate.m :as horizon-coordinate.m]
   [astronomy.objects.astronomical-coordinate.m :as astronomical-coordinate.m]
   [astronomy.objects.terrestrial-coordinate.m :as terrestrial-coordinate.m]))


(def schema
  (merge
   m.astro-scene/schema
   m.celestial/schema
   m.circle-orbit/schema
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
   m.goto-tool/schema
   m.ppt-tool/schema
   m.ruler-tool/schema
   ))


(def basic-db
  (let [conn (d/create-conn (merge mtd-model/schema schema))]
    @conn))


(comment
  (d/pull basic-db '[*] [:person/name "dr who"])
  (count (merge mtd-model/schema schema)) 
  )