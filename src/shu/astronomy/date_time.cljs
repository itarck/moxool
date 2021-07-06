(ns shu.astronomy.date-time
  (:require
   [cljs-time.core :as t]
   [cljs-time.format :as ft]))

;; cljs-time 的 datetime 格式和 epoch-days的换算
;; epoch-days是从 j2000.0历元开始计算的时间，正的是往后数，复数是往前数的天数


(def j2000 (t/date-time 2000 1 1 11 58 55 816))

(def zero-year (t/minus (t/date-time 2000 1 1) (t/years 2000)))

(def one-day-in-millis (* 86400 1000))

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

(def custom-formatter (ft/formatter "MM月dd日 HH:mm:ss"))

(defn format-string [date-time]
  (if (t/after? date-time zero-year)
    (str "公元" (t/year date-time) "年 " (ft/unparse custom-formatter date-time))
    (str "公元前" (- (t/year date-time)) "年 " (ft/unparse custom-formatter date-time))))




(comment


  (format-string (from-epoch-days (* 365 -2100)))
  (format-string (from-epoch-days (* 365 2100)))

  (ft/unparse custom-formatter (from-epoch-days (* 365 -3100)))

  
  (t/year (from-epoch-days (* 365 -3100)))

  (from-epoch-days (* 365 -3100))
  
  (to-epoch-days (t/date-time 2010 1 1))
  (to-epoch-days (t/date-time 5000 1 1))

  (format-string 5000)

  (def d1 (t/date-time 2000 03 20 07 35 14))
  (def d2 (t/date-time 2002 03 20 19 16))
  (def d3 (t/date-time 2003 03 21 00 59))
  
  (to-epoch-days d1)
  ;; => 444.06324287037035

  (to-epoch-days d2)
  ;; => 809.3035206481482

  
  (- (to-epoch-days d2) (to-epoch-days d1))

  (- (to-epoch-days d3) (to-epoch-days d2))

  (- (to-epoch-days (t/date-time 2001 03 20 13 30))
     365.242190419)

  (to-epoch-days vernal-equinox)
  ;; => 78.81687712962963



  )