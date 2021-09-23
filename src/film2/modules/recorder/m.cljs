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
              :current-iovideo -202})


(def schema {:recorder/name {:db/unique :db.unique/identity}
             :recorder/current-iovideo {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})




(defn pull-one [db id]
  (d/pull db '[*] id))

(defn sub-recorder [system-conn id]
  @(p/pull system-conn '[* {:editor/player [*]}] id))


;; txs

(defn create-video-tx [editor initial-db-str]
  [#:video {:db/id -1
            :scene (-> editor :editor/scene :db/id)
            :name (str "clip-" (shu.date-time/current-date-time-string!))
            :start-timestamp (shu.timestamp/current-timestamp!)
            :total-time 0
            :initial-db-str initial-db-str
            :tx-logs []}
   [:db/add (:db/id editor) :editor/current-video -1]])

(defn open-video-tx [editor video-id]
  [[:db/add (:db/id editor) :editor/current-video video-id]])


;; process 


(defn create-video! [system-conn editor-id initial-db-str]
  (let [editor (pull-one @system-conn editor-id)
        tx (create-video-tx editor initial-db-str)]
    (p/transact! system-conn tx)))


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


