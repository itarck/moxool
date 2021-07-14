(ns astronomy.model.clock
  (:require
   [goog.string :as gstring]
   [cljs.spec.alpha :as s]
   [shu.goog.math :as gmath]
   [shu.three.spherical :as sph]
   [shu.calendar.date-time :as dt]
   [shu.calendar.epoch :as epoch]))


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


(defn cal-longitude [position]
  (let [[px py pz] position
        [r phi theta] (vec (sph/from-cartesian-coords px py pz))]
    (->
     (+ theta Math/PI)
     (gmath/standard-angle-in-radians)
     (gmath/to-degree)
     (- 102))))


(defn cal-local-time [standard-time-in-days longitude]
  (+ standard-time-in-days (/ longitude 360.0)))


(defn set-clock-time-in-days-tx [clock-id time-in-days]
  [[:db/add clock-id :clock/time-in-days time-in-days]])


(defn utc-format-string [epoch-days]
  (dt/format-string (epoch/from-epoch-days epoch-days)))


(comment
  
  (gstring/format "%2.0f" 2)

  ;; 
  )