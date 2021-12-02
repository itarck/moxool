(ns film2.data.ioframe
  (:require
   [datascript.transit :as dt]
   [astronomy.system.city :as sys.city])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))



(def mini-1
  #:ioframe {:type :mini
             :name "mini-1"
             :db-transit-str (read-resource "private/frame/default.fra")
             :description "只有太阳和地球的小型系统"})

(def mini-2
  #:ioframe {:type :mini
             :name "mini-2"
             :db-transit-str (read-resource "private/frame/solar-0.0.3.fra")
             :description "只有太阳和地球的小型系统"})

(def city
  #:ioframe {:type :city
             :name "city-1"
             :db-transit-str (dt/write-transit-str sys.city/db)
             :description "一个城市"})
