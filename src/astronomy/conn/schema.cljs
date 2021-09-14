(ns astronomy.conn.schema
  (:require
   [methodology.model.core :as mtd-model]

   [astronomy.space.user.m :as user.m]
   [astronomy.space.backpack.m :as m.backpack]
   [astronomy.space.camera.m :as camera.m]

   [astronomy.tools.universe-tool.m :as m.universe-tool]
   [astronomy.tools.clock-tool.m :as m.clock-tool]
   [astronomy.tools.info-tool.m :as m.info-tool]
   [astronomy.tools.spaceship-camera-control.m :as m.spaceship]
   [astronomy.tools.ppt-tool.m :as m.ppt-tool]
   [astronomy.tools.ruler-tool.m :as m.ruler-tool]

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
   [astronomy.objects.terrestrial-coordinate.m :as terrestrial-coordinate.m]))


(def schema
  (merge
   mtd-model/schema

   user.m/schema
   m.backpack/schema
   camera.m/schema

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

