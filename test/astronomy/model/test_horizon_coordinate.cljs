(ns astronomy.model.test-horizon-coordinate
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   
   [astronomy.conn.core :refer [create-basic-conn!]]
   [posh.reagent :as p]
   [astronomy.data.celestial :as d.celestial]
   [astronomy.data.coordinate :as d.coordinate]
   [astronomy.model.astro-scene :as m.astro-scene]
   [methodology.model.object :as m.object]
   [astronomy.model.clock :as m.clock]
   [astronomy.model.horizon-coordinate :as m.horizon-coordinate]))


(def conn 
  (let [conn (create-basic-conn!)]
    (p/transact! conn d.celestial/dataset1)
    (p/transact! conn d.coordinate/dataset1)
    conn))


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

