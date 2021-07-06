(ns astronomy.model.clock
  (:require
   [goog.string :as gstring]
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


(defn quot' [a b]
  (int (/ a b)))

(defn rem' [a b]
  (- a (* b (quot' a b))))

(defn parse-time-in-days [time-in-days]
  (let [quot-years (quot' time-in-days year)
        rem-years (rem' time-in-days year)
        quot-days (quot' rem-years day)
        rem-days (rem' rem-years day)
        quot-hours (quot' rem-days hour)
        rem-hours (rem' rem-days hour)
        quot-minutes (quot' rem-hours minute)
        rem-minutes (rem' rem-hours minute)
        seconds (/ rem-minutes second')]
    {:years quot-years
     :days quot-days
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


(defn format-time-in-days [time-in-days]
  (let [{:keys [years days minutes hours seconds]} (parse-time-in-days time-in-days)]
    (str "第" years "年，"
         "第" days "天，"
         (if (and (> hours 0) (< hours 10)) (str "0" hours) hours) ":"
         (if (< minutes 10) (str "0" minutes) minutes) ":"
         (when (< (int seconds) 10) "0")
         (gstring/format "%0.3f" (/ (int (* 1000 seconds)) 1000)))))



(comment
  
  (gstring/format "%2.0f" 2)

  ;; 
  )