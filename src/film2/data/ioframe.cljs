(ns film2.data.ioframe
  (:require
   [datascript.transit :as dt]
   [astronomy.system.city :as sys.city])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))



(def mini-1
  #:ioframe {:type :solar
             :name "1.太阳和地球的小型系统"
             :db-transit-str (read-resource "private/frame/default.fra")
             :description "只有太阳和地球的小型系统"})

(def mini-2
  #:ioframe {:type :solar
             :name "2.太阳、地球和五大行星"
             :db-transit-str (read-resource "private/frame/solar-0.0.3.fra")
             :description "2.太阳、地球和五大行星"})

(def solar-planet5
  #:ioframe {:type :solar
             :name "3.太阳、地球和五大行星（显示轨道）"
             :db-transit-str (read-resource "private/frame/dev-20211202-1753.fra")
             :description "2.太阳、地球和五大行星"})


(def city
  #:ioframe {:type :city
             :name "city-1"
             :db-transit-str (dt/write-transit-str sys.city/db)
             :description "一个城市"})

(def scene-1-1
  #:ioframe {:type :solar
             :name "场景1.1：地球和恒星背景"
             :db-transit-str (read-resource "private/frame/angel/scene-1-1-v20211203.fra")}
  )


