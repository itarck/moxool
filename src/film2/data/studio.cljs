(ns film2.data.studio
  (:require 
   [datascript.transit :as dt]
   [astronomy.system.slider :as slider]
   [astronomy.conn.mini-factory :as mini-factory]))


(def mini-db-str (dt/write-transit-str (mini-factory/create-db2)))

(def slider-db-str (dt/write-transit-str slider/db))

(def dataset 
  [#:ioframe {:type :mini
              :name "mini-1"
              :db-transit-str mini-db-str
              :description "只有太阳和地球的小型系统"}
   #:ioframe {:type :mini
              :name "mini-2"
              :db-transit-str mini-db-str
              :description "只有太阳和地球的小型系统"}
   #:ioframe {:db-transit-str slider-db-str
              :type :slider
              :name "slider-1"
              :description "一个进度条"}
   #:iovideo {:name "slider move"
              :start-timestamp 124234
              :stop-timestamp 534543
              :total-time 3000
              :ioframe #:ioframe {:db-transit-str slider-db-str
                                  :type :slider
                                  :name "slider-1"
                                  :description "一个进度条"}
              :tx-logs [{:relative-time 1000
                         :tx-data [#:slider{:name "bmi"
                                            :value 80}]}
                        {:relative-time 2000
                         :tx-data [#:slider{:name "bmi"
                                            :value 20}]}]}
   #:iovideo {:name "camera move"
              :start-timestamp 124234
              :stop-timestamp 534543
              :total-time 3000
              :ioframe [:ioframe/name "slider-1"]
              :tx-logs [{:relative-time 1000
                         :tx-data [#:camera{:name "default"
                                            :position [1000 900 1000]
                                            :rotation [0 0 0]}]}
                        {:relative-time 2000
                         :tx-data [#:camera{:name "default"
                                            :position [1000 2000 1000]
                                            :rotation [0 0 0]}]}]}
   
   #:studio {:name "default"
             :mode :editor
             :editor #:editor {:name "default"
                               :doc "编辑ioframe的工具"
                               :current-ioframe [:ioframe/name "mini-2"]}
             :player #:player {:name "default"
                               :doc "播放iovideo的工具"
                               :current-iovideo [:iovideo/name "slider move"]}
             :recorder #:recorder {:name "default"
                                   :current-iovideo [:iovideo/name "slider move"]}}])

