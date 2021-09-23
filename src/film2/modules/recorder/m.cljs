(ns film2.modules.recorder.m
  (:require
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [shu.calendar.timestamp :as shu.timestamp]
   [shu.calendar.date-time :as shu.date-time]
   [film2.modules.iovideo.m :as iovideo.m]))



(def sample
  #:recorder {:db/id -2
              :name "default"
              :current-menu :create-iovideo
              :current-iovideo -202})


(def schema {:recorder/name {:db/unique :db.unique/identity}
             :recorder/current-iovideo {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})


(defn pull-one [db id]
  (d/pull db '[*] id))

(defn sub-recorder [conn id]
  @(p/pull conn '[*] id))


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
              :tx-logs []}
   [:db/add (:db/id recorder) :recorder/current-iovideo -1]])


(defn open-video-tx [editor video-id]
  [[:db/add (:db/id editor) :editor/current-video video-id]])


;; process 


(defn start-record! [system-conn scene-conn editor-id]
  (let [editor (pull-one @system-conn editor-id)
        video (iovideo.m/pull-one @system-conn (-> editor :editor/current-video :db/id))]
    (p/transact! system-conn [#:video {:db/id (:db/id video)
                                       :initial-db-transit (dt/write-transit-str @scene-conn)
                                       :start-timestamp (shu.timestamp/current-timestamp!)
                                       :total-time 0
                                       :tx-logs []}])
    (d/listen! scene-conn (str "record" (:db/id video))
               (fn [log]
                 (iovideo.m/iovideo-append-tx-log! system-conn (:db/id video) (:tx-data log))))))


(defn stop-record! [system-conn scene-conn editor-id]
  (let [editor (pull-one @system-conn editor-id)
        video (iovideo.m/pull-one @system-conn (-> editor :editor/current-video :db/id))]
    (p/transact! system-conn [(iovideo.m/update-stop-timestamp video)])
    (d/unlisten! scene-conn (str "record" (:db/id video)))))


