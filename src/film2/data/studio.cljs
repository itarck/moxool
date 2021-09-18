(ns film2.data.studio
  (:require 
   [astronomy.system.slider :as slider]))


(def dataset 
  [#:ioframe {:type :mini
              :name "mini-1"
              :db-url "/temp/frame/solar-1.fra"
              :description "只有太阳和地球的小型系统"}
   #:ioframe {:type :mini
              :name "mini-2"
              :db-url "/temp/frame/solar-2.fra"
              :description "只有太阳和地球的小型系统"}
   #:ioframe {:db slider/db
              :type :slider
              :name "slider-1"
              :description "一个进度条"}
   #:iovideo {:name "slider move"
              :start-time 124234
              :stop-time 534543
              :total-time 3000
              :ioframe #:ioframe {:db slider/db
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
              :start-time 124234
              :stop-time 534543
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
                               :current-frame [:ioframe/name "mini-2"]}
             :player #:player {:name "default"
                               :doc "播放iovideo的工具"
                               :current-video [:iovideo/name "video-1"]}
             :recorder #:recorder {:name "default"
                                   :current-video [:iovideo/name "video-1"]}}])

