(ns film2.modules.recorder.m
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.calendar.timestamp :as shu.timestamp]
   [film2.modules.iovideo.m :as iovideo.m]))



(def sample
  #:recorder {:db/id -2
              :name "default"
              :current-menu :create-iovideo
              :current-iovideo -202})


(def schema {:recorder/name {:db/unique :db.unique/identity}
             :recorder/current-iovideo {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})



(def menu-ident-and-names
  [[:create-iovideo "新建iovideo"]
   [:copy-ioframe "复制ioframe"]
   [:edit-ioframe "编辑ioframe"]
   [:record "录制iovideo"]
   [:upload-mp3 "上传map3"]
   [:export-iovideo "打包iovideo"]])

;; txs

(defn create-iovideo-tx [recorder new-name]
  [#:iovideo {:db/id -1
              :name new-name
              :initial-ioframe #:ioframe {:name (str new-name "-initial")}
              :tx-logs []}
   #:recorder {:db/id (:db/id recorder)
               :recorder/current-iovideo -1}])


