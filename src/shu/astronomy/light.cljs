(ns shu.astronomy.light
  (:require
   [goog.string :as gstring]))


(def light-second-unit 1)
(def light-hour-unit 3600)
(def light-day-unit (* light-hour-unit 24))
(def light-year-unit (* 365 light-day-unit))


(defn semantic-distance-in-light-seconds [d]
  (cond
    (< d light-hour-unit) (str (gstring/format "%.1f" d) " 光秒")
    (and (>= d light-hour-unit) (< d light-day-unit)) (str (gstring/format "%.1f" (/ d light-hour-unit)) " 光时")
    (and (>= d light-day-unit) (< d light-year-unit)) (str (gstring/format "%.1f" (/ d light-day-unit))  " 光日")
    (and (>= d light-year-unit) (< d (* 10000 light-year-unit))) (str (gstring/format "%.1f" (/ d light-year-unit)) " 光年")
    (and (>= d (* 10000 light-year-unit)) (< d (* 100000000 light-year-unit))) (str (gstring/format "%.1f" (/ d light-year-unit 10000)) " 万光年")
    :else (str (gstring/format "%.1f" (/ d light-year-unit 100000000)) " 亿光年")))


(comment 
  
  light-year-unit
  (semantic-distance-in-light-seconds 26222342343343)
  
  )