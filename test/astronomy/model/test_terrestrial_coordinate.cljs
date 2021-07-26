(ns astronomy.model.test-terrestrial-coordinate
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [astronomy.scripts.test-conn :as test-conn]
   [posh.reagent :as p]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.object :as m.object]
   [astronomy.model.clock :as m.clock]
   [astronomy.model.terrestrial-coordinate :as m.terrestrial-coordinate]))


(def conn (test-conn/init-conn!))


(def terr-coor
  @(p/pull conn '[*] [:coordinate/name "地球坐标系"]))

terr-coor

(p/transact! conn (m.clock/set-clock-time-in-days-tx [:clock/name "default"] 5))

(def scene @(p/pull conn '[*] [:scene/name "solar"]))

(p/transact! conn (m.astro-scene/refresh-tx @conn scene))

(p/transact! conn (m.terrestrial-coordinate/update-position-and-quaternion-tx @conn terr-coor))


(def terr-coor
  @(p/pull conn '[*] [:coordinate/name "地球坐标系"]))

(m.object/cal-invert-matrix terr-coor)

