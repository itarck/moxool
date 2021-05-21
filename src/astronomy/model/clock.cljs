(ns astronomy.model.clock
  (:require
   [cljs.spec.alpha :as s]
   [shu.goog.math :as gmath]
   [shu.three.spherical :as sph]))


(def day 1)
(def hour (/ day 24))
(def minute (/ hour 60))
(def second' (/ minute 60))
(def week (* 7 day))
(def year (* 365 day))


;; model

(def schema {:clock/name {:db/unique :db.unique/identity}})

(s/def :clock/time-in-days float?)
(s/def :clock/name string?)
(s/def :astronomy/clock (s/keys :req [:clock/time-in-days]
                                :opt [:clock/name]))

(def sample
  #:clock {:name "default"
           :time-in-days 0.0})

(s/valid? :astronomy/clock sample)


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


(defn cal-longitude [position]
  (let [[px py pz] position
        [r phi theta] (vec (sph/from-cartesian-coords px py pz))]
    (->
     (+ theta Math/PI)
     (gmath/standard-angle-in-radians)
     (gmath/to-degree))))


(defn cal-local-time [standard-time-in-days longitude]
  (+ standard-time-in-days (/ longitude 360.0)))


(defn set-clock-time-in-days-tx [clock-id time-in-days]
  [[:db/add clock-id :clock/time-in-days time-in-days]])


(comment

  (parse-time-in-days 34.4234)
  ;; => {:days 34, :hours 10, :minutes 9, :seconds 41.76000000007986}

  (sph/from-cartesian-coords 0 0 -1)

  ;; 
  )