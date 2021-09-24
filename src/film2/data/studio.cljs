(ns film2.data.studio
  (:require 
   [datascript.transit :as dt]
   [astronomy.system.slider :as sys.slider]
   [astronomy.system.city :as sys.city]
   [astronomy.conn.mini-factory :as mini-factory]))


(def mini-db-str (dt/write-transit-str (mini-factory/create-db2)))

(def slider-db-str (dt/write-transit-str sys.slider/db))

(def city-db-str (dt/write-transit-str sys.city/db))

(def dataset 
  [#:ioframe {:type :mini
              :name "mini-1"
              :db-transit-str mini-db-str
              :description "只有太阳和地球的小型系统"}
   #:ioframe {:type :slider
              :name "slider-0"
              :db-transit-str slider-db-str
              :description "一个进度条"}
   #:ioframe {:type :city
              :name "city-1"
              :db-transit-str city-db-str
              :description "一个城市"}
   #:iovideo {:name "slider move"
              :start-timestamp 124234
              :stop-timestamp 534543
              :total-time 3000
              :initial-ioframe #:ioframe {:type :slider
                                          :name "video-1-initial"
                                          :db-transit-str slider-db-str
                                          :description "一个进度条"}
              :tx-logs [{:relative-time 1000
                         :tx-data [#:slider{:name "bmi"
                                            :value 80}]}
                        {:relative-time 2000
                         :tx-data [#:slider{:name "bmi"
                                            :value 20}]}]}
   #:iovideo {:name "city camera move"
              :start-timestamp 124234
              :stop-timestamp 534543
              :total-time 3000
              :initial-ioframe #:ioframe {:type :city
                                          :name "city-1"
                                          :db-transit-str city-db-str
                                          :description "一个城市"}
              :tx-logs [{:relative-time 1000
                         :tx-data [#:camera{:name "default"
                                            :position [2000 2000 1000]}]}
                        {:relative-time 2000
                         :tx-data [#:camera{:name "default"
                                            :position [3000 2000 1000]}]}]}
   #:iovideo {:name "mini move"
              :start-timestamp 124234
              :stop-timestamp 534543
              :total-time 3000
              :initial-ioframe #:ioframe {:type :mini
                                          :name "mini-initial"
                                          :db-transit-str mini-db-str
                                          :description "只有太阳和地球的小型系统"}
              :tx-logs [{:relative-time 1000
                         :tx-data [#:camera{:name "default"
                                            :position [2000 2000 2000]
                                            :quaternion [0 0 0 1]}]}
                        {:relative-time 2000
                         :tx-data [#:camera{:name "default"
                                            :position [2000 0 0]
                                            :quaternion [0 0 0 1]}]}]}
   
   #:studio {:name "default"
             :mode :editor
             :editor #:editor {:name "default"
                               :doc "编辑ioframe的工具"
                               :current-ioframe [:ioframe/name "mini-1"]}
             :player #:player {:name "default"
                               :doc "播放iovideo的工具"
                               :current-iovideo [:iovideo/name "slider move"]}
             :recorder #:recorder {:name "default"
                                   :doc "编辑和录制iovideo的工具"
                                   :current-menu :create-iovideo
                                   :current-iovideo [:iovideo/name "slider move"]}}])

