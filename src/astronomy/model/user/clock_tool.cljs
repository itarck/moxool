(ns astronomy.model.user.clock-tool
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.model.clock :as m.clock]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.model.coordinate :as m.coordinate]))



(def clock-tool1
  #:clock-tool {:db/id -2
                :steps-per-second 50
                :status :stop
                :step-interval :hour
                :days-per-step (/ 1 24)
                :clock #:clock {:name "default"
                                :time-in-days 0}
                :tool/name "clock control"
                :tool/chinese-name "时钟1"
                :tool/backpack -3
                :entity/type :clock-tool})

(def schema {:clock-tool/clock {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})


(defn pull-clock-tool [db id]
  (d/pull db '[* {:clock-tool/clock [*]}] id))

(defn tick-clock [clock-tool clock]
  (+ (:clock-tool/days-per-step clock-tool)
     (get-in clock [:clock/time-in-days])))


(defn update-by-clock-time-tx [db clock-id time-in-days]
  (let [tx0 (m.clock/set-clock-time-in-days-tx clock-id time-in-days)
        db1 (d/db-with db tx0)
        celes (m.celestial/find-all-by-clock db1 clock-id)
        tx1 (mapcat #(m.celestial/update-position-and-quaternion-tx %) celes)
        db2 (d/db-with db1 tx1)
        coordinate-ids (m.coordinate/find-ids-by-clock db1 clock-id)
        tx2 (mapcat #(m.coordinate/update-coordinate-tx db2 %) coordinate-ids)]
    (concat tx0 tx1 tx2)))

