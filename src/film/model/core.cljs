(ns film.model.core
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.calendar.timestamp :as shu.timestamp]
   [film.model.editor :as m.editor]
   [film.model.player :as m.player]
   [film.model.scene :as m.scene]
   [film.model.video :as m.video]))


(def schema
  (merge
   m.editor/schema
   m.scene/schema
   m.player/schema
   m.video/schema))

schema


(def editor1
  #:editor {:name "default"
            :scene -101})

(def player1
  #:player {:name "default"
            :current-video -202})

(def scene1
  #:scene {:db/id -101
           :name "default"})

(def video1
  #:video {:db/id -202
           :scene -101
           :name "default"
           :start-timestamp (shu.timestamp/current-timestamp!)
           :total-time 3000
           :tx-logs [{:relative-time 500
                      :tx-data [{:slider/name "bmi"
                                 :slider/value 30}]}
                     {:relative-time 1000
                      :tx-data [{:slider/name "bmi"
                                 :slider/value 80}]}]})


(defn create-empty-conn! []
  (let [conn (d/create-conn schema)]
    (p/posh! conn)
    conn))


(defn create-basic-conn! []
  (let [conn (d/create-conn schema)]
    (d/transact! conn [editor1 player1 scene1 video1])
    (p/posh! conn)
    conn))


(def empty-db 
  (let [conn (create-empty-conn!)]
    @conn))

(def basic-db
  (let [conn (create-basic-conn!)]
    @conn))



