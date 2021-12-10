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

;; angel version 


(def db-url
  {:base "frame/dev/base-v1.fra"
   :scene-1-1 "frame/dev/scene-1-1-v2.fra"
   :scene-1-2 "frame/dev/scene-1-2-v2.fra"
   :scene-1-3 "frame/dev/scene-1-3-v2.fra"
   :scene-2-1 "frame/dev/scene-2-1-v1.fra"
   :scene-2-2 "frame/dev/scene-2-2-v1.fra"
   :scene-2-3 "frame/dev/scene-2-3-v1.fra"
   :scene-3-1 "frame/dev/scene-3-1-v1.fra"
   :scene-3-2 "frame/dev/scene-3-2-v1.fra"
   :scene-3-3 "frame/dev/scene-3-3-v2.fra"
   :scene-4-1 "frame/dev/scene-4-1-v1.fra"
   :scene-4-2 "frame/dev/scene-4-2-v1.fra"})


(def scene-baseline
  #:ioframe {:type :solar
             :name "场景0：基础数据库"
             :db-transit-str (read-resource "private/frame/temp/dev-20211206-7.fra")})

(def scene-1-1
  #:ioframe {:type :solar
             :name "场景1.1：地球和恒星背景"
             :db-transit-str (read-resource "frame/dev/scene-1-1-v2.fra")})

(def scene-1-2
  #:ioframe {:type :solar
             :name "场景1.2：天球背景和星座"
             :db-transit-str (read-resource "frame/dev/scene-1-2-v2.fra")})

(def scene-1-3
  #:ioframe {:type :solar
             :name "场景1.3：天球坐标系、地球坐标系和地面坐标系"
             :db-transit-str (read-resource "frame/dev/scene-1-3-v2.fra")})

(def scene-2-1
  #:ioframe {:type :solar
             :name "场景2.1：地球和太阳，观察日和夜"
             :db-transit-str (read-resource "frame/dev/scene-2-1-v1.fra")})

(def scene-2-2
  #:ioframe {:type :solar
             :name "场景2.2：地球和太阳，黄道，观察季节"
             :db-transit-str (read-resource "frame/dev/scene-2-2-v1.fra")})

(def scene-2-3
  #:ioframe {:type :solar
             :name "场景2.3：天球坐标系中心的移动"
             :db-transit-str (read-resource "frame/dev/scene-2-3-v1.fra")})


(def scene-3-1
  #:ioframe {:type :solar
             :name "场景3.1：日心说，太阳中心视角下的五大行星"
             :db-transit-str (read-resource "frame/dev/scene-3-1-v1.fra")})

(def scene-3-2
  #:ioframe {:type :solar
             :name "场景3.2：地心说，地球中心视角下的五大行星"
             :db-transit-str (read-resource "frame/dev/scene-3-2-v1.fra")})

(def scene-3-3
  #:ioframe {:type :solar
             :name "场景3.3：地轴的进动"
             :db-transit-str (read-resource "frame/dev/scene-3-3-v2.fra")})

(def scene-4-1
  #:ioframe {:type :solar
             :name "场景4.1：在地球上观察月相和月球轨道的变化"
             :db-transit-str (read-resource "frame/dev/scene-4-1-v1.fra")})

(def scene-4-2
  #:ioframe {:type :solar
             :name "场景4.2：三体问题，月球轨道的进动"
             :db-transit-str (read-resource "frame/dev/scene-4-2-v1.fra")})

(def scene-full
  #:ioframe {:type :solar
             :name "探索场景"
             :db-transit-str (read-resource "frame/dev/full-v1.fra")})