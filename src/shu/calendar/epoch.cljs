(ns shu.calendar.epoch
  (:require
   [cljs-time.core :as t]))

;; cljs-time 的 datetime 格式和 epoch-days的换算
;; epoch-days是从 j2000.0历元开始计算的时间，正的是往后数，复数是往前数的天数

(def one-day-in-millis (* 86400 1000))

(def j2000
  (t/date-time 2000 1 1 11 58 55 816))

(def vernal-equinox
  (t/date-time 2000 03 20 07 35 14))

(defn to-epoch-days [date-time]
  (if (t/after? date-time j2000)
    (/ (t/in-millis (t/interval j2000 date-time)) one-day-in-millis)
    (- (/ (t/in-millis (t/interval date-time j2000)) one-day-in-millis))))

(defn from-epoch-days [epoch-days]
  (if (>= epoch-days 0)
    (t/plus j2000 (t/millis (* one-day-in-millis epoch-days)))
    (t/minus j2000 (t/millis (* one-day-in-millis (- epoch-days))))))



