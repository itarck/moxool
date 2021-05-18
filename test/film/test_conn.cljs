(ns film.test-conn
  (:require
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [shu.general.time :as time]
   [film.model.core :refer [schema]]))





(defn create-scene-conn! []
  (let [conn (d/create-conn {:slider/name {:db/unique :db.unique/identity}})]
    (d/transact! conn [{:slider/name "bmi"
                        :slider/value 50}])
    (p/posh! conn)
    conn))


(def scene-db @(create-scene-conn!))


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
           :start-timestamp (time/get-timestamp)
           :total-time 3000
           :initial-db-str (dt/write-transit-str scene-db)
           :tx-logs [{:relative-time 500
                      :tx-data [{:slider/name "bmi"
                                 :slider/value 30}]}
                     {:relative-time 1000
                      :tx-data [{:slider/name "bmi2"
                                 :slider/value 80}]}]})


(def video2
  #:video {:db/id -203
           :scene -101
           :name "another"
           :start-timestamp (time/get-timestamp)
           :total-time 3000
           :initial-db-str (dt/write-transit-str scene-db)
           :tx-logs [{:relative-time 100
                      :tx-data [{:slider/name "bmi"
                                 :slider/value 30}]}
                     {:relative-time 500
                      :tx-data [{:slider/name "bmi"
                                 :slider/value 80}]}]})




(defn create-system-conn! []
  (let [conn (d/create-conn schema)]
    (d/transact! conn [editor1 player1 scene1 video1 video2])
    (p/posh! conn)
    conn))

