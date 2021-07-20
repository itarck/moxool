(ns astronomy.model.test-horizon-coordinate
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [astronomy.scripts.test-conn :refer [create-test-conn!]]
   [posh.reagent :as p]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.object :as m.object]
   [astronomy.model.clock :as m.clock]
   [astronomy.model.horizon-coordinate :as m.horizon-coordinate]))


(def conn (create-test-conn!))


(def horizon-coor
  @(p/pull conn '[*] [:coordinate/name "地平坐标系"]))

horizon-coor

(p/transact! conn (m.clock/set-clock-time-in-days-tx [:clock/name "default"] 5))

(def scene @(p/pull conn '[*] [:scene/name "solar"]))

(p/transact! conn (m.astro-scene/refresh-tx @conn scene))

(p/transact! conn (m.horizon-coordinate/update-position-and-quaternion-tx @conn [:coordinate/name "地平坐标系"]))


(def horizon-coor-2
  @(p/pull conn '[*] [:coordinate/name "地平坐标系"]))

(m.object/cal-invert-matrix horizon-coor-2)

