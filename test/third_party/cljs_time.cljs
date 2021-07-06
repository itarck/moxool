(ns test.third-party.cljs-time
  (:require
   [cljs-time.core :as t]
   [cljs-time.format :as ft]
   [cljs-time.coerce :as ct])
  (:import goog.date.UtcDateTime))


(def t1 (t/date-time 2021 1 1 10 0 0))

(def t2 (t/date-time 2022 1 1 10 10 0))

(/ (t/in-millis (t/interval t1 t2)) 86400000)

(t/today)

(t/minutes 30)

(t/plus t1 (t/minutes 30))

(def built-in-formatter (ft/formatters :basic-date-time))

(def custom-formatter (ft/formatter "yyyy-MM-dd HH:mm:ss"))


(t/after? t2 t1)

(ct/to-long (t/date-time -1 4 25))

(ct/from-long 0)