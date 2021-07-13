(ns shu.calendar.date-time
  (:require
   [cljs-time.core :as t]
   [cljs-time.format :as ft]))


(def zero-year (t/minus (t/date-time 2000 1 1) (t/years 2000)))

(def custom-formatter (ft/formatter "MM月dd日 HH:mm:ss"))

(defn format-string [date-time]
  (if (t/after? date-time zero-year)
    (str "公元" (t/year date-time) "年 " (ft/unparse custom-formatter date-time))
    (str "公元前" (- (t/year date-time)) "年 " (ft/unparse custom-formatter date-time))))

(defn current-date-time! []
  (t/now))

(defn current-date-time-string! []
  (format-string (current-date-time!)))

