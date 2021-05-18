(ns astronomy.model.clock
  (:require
   [cljs.core.async :refer [go >! <!] :as a]
   [datascript.core :as d]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.model.coordinate :as m.ref]
   [posh.reagent :as p]))


(def day 1)
(def hour (/ day 24))
(def minute (/ hour 60))
(def second' (/ minute 60))
(def week (* 7 day))
(def year (* 365 day))


(def sample
  #:clock {:name "default"
           :time-in-days 0.0})


;; model

(def schema {:clock/name {:db/unique :db.unique/identity}})


(defn parse-time-in-days [time-in-days]
  (let [quot-days (quot time-in-days day)
        rem-days (rem time-in-days day)
        quot-hours (quot rem-days hour)
        rem-hours (rem rem-days hour)
        quot-minutes (quot rem-hours minute)
        rem-minutes (rem rem-hours minute)
        seconds (/ rem-minutes second')]
    {:days quot-days
     :hours quot-hours
     :minutes quot-minutes
     :seconds seconds}))


(defn set-clock-time-in-days-tx [clock time-in-days]
  [[:db/add (:db/id clock) :clock/time-in-days time-in-days]])

(defn update-celestial-by-clock-tx [db clock-id]
  (let [clock (d/pull db '[*] clock-id)
        celes (m.celestial/find-celestials-by-clock db clock)]
    (mapcat #(m.celestial/update-position-and-quaternion-tx % clock) celes)))

(defn update-reference-tx [db]
  (let [id [:coordinate/name "default"]]
    [[:db/add id :coordinate/position (m.ref/cal-world-position db id)]
     [:db/add id :coordinate/quaternion (m.ref/cal-world-quaternion db id)]]))



(comment 
  
  (parse-time-in-days 34.4234)
  ;; => {:days 34, :hours 10, :minutes 9, :seconds 41.76000000007986}
  
  )