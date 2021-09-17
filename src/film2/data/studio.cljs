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
              :name "slider1-1"
              :description "一个进度条"}
   #:editor{:name "default"
            :status :init
            :current-frame [:ioframe/name "mini-2"]}])

