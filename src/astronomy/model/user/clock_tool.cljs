(ns astronomy.model.user.clock-tool
  (:require 
   [datascript.core :as d]
   [posh.reagent :as p]))



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

(defn tick-clock [clock-tool]
  (+ (:clock-tool/days-per-step clock-tool)
     (get-in clock-tool [:clock-tool/clock :clock/time-in-days])))

