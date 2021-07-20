(ns astronomy.model.core
  (:require
   [datascript.core :as d]
   [methodology.model.core :as mtd-model]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.reference :as m.reference]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.model.circle-orbit :as m.circle-orbit]
   [astronomy.model.clock :as m.clock]
   [astronomy.model.galaxy :as m.galaxy]
   [astronomy.model.planet :as m.planet]
   [astronomy.model.satellite :as m.satellite]
   [astronomy.model.spin :as m.spin]
   [astronomy.model.star :as m.star]
   [astronomy.model.constellation :as m.constellation]
   [astronomy.model.atmosphere :as m.atmosphere]
   [astronomy.model.horizontal-coordinate :as m.horizon]
   [astronomy.model.coordinate :as m.coordinate]
   [astronomy.model.astronomical-coordinate :as m.astronomical-coordinate]
   [astronomy.model.terrestrial-coordinate :as m.terrestrial-coordinate]

   [astronomy.model.user.universe-tool :as m.universe-tool]
   [astronomy.model.user.clock-tool :as m.clock-tool]
   [astronomy.model.user.info-tool :as m.info-tool]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   [astronomy.model.user.goto-celestial-tool :as m.goto-tool]
   [astronomy.model.user.ppt-tool :as m.ppt-tool]
   
   ))


(def schema
  (merge
   m.astro-scene/schema
   m.reference/schema
   m.celestial/schema
   m.circle-orbit/schema
   m.clock/schema
   m.galaxy/schema
   m.planet/schema
   m.satellite/schema
   m.spin/schema
   m.star/schema
   m.constellation/schema
   m.atmosphere/schema
   m.horizon/schema
   m.coordinate/schema 
   m.astronomical-coordinate/schema
   m.terrestrial-coordinate/schema

   m.universe-tool/schema
   m.clock-tool/schema
   m.spaceship/schema
   m.info-tool/schema
   m.goto-tool/schema
   m.ppt-tool/schema))


(def basic-db
  (let [conn (d/create-conn (merge mtd-model/schema schema))]
    @conn))


(comment
  (d/pull basic-db '[*] [:person/name "dr who"])
  (count (merge mtd-model/schema schema)) 
  )