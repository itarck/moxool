(ns film2.modules.player.m
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]))


(def sample
  #:player {:db/id -2
            :name "default"
            :current-video -202})

(def schema {:player/name {:db/unique :db.unique/identity}
             :player/current-iovideo {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})


;; model

(defn pull-whole [db id]
  (d/pull db '[* {:player/current-iovideo [*]}] id))

(defn cal-current-time [player-session current-timestamp]
  (let [{:keys [start-time start-timestamp]} player-session]
    (+ (- current-timestamp start-timestamp) start-time)))

(defn should-session-stop? [player video]
  (let [total-time (get-in video [:iovideo/total-time])
        current-time (get-in player [:player/session :current-time])]
    (> current-time total-time)))

(defn check-session-starting? [player]
  (= (get-in player [:player/session :mode]) :start))

(defn check-session-paused? [player]
  (= (get-in player [:player/session :mode]) :pause))

(defn check-session-stoped? [player]
  (= (get-in player [:player/session :mode]) :stop))

;; tx

(defn create-session-tx [player video]
  [{:db/id (:db/id player)
    :player/session {:mode :stop
                     :start-time 0
                     :current-time 0
                     :total-time (:video/total-time video)}}])

(defn start-session-tx [player current-timestamp]
  (let [session (-> (:player/session player)
                    (assoc :mode :start)
                    (assoc :start-timestamp current-timestamp))]
    [[:db/add (:db/id player) :player/session session]]))

(defn update-session-tx [player current-timestamp]
  (let [old-session (:player/session player)
        current-time (+ (- current-timestamp (:start-timestamp old-session)) (:start-time old-session))
        new-session (-> old-session
                        (assoc :current-time current-time)
                        (assoc :current-timestamp current-timestamp))]
    [[:db/add (:db/id player) :player/session new-session]]))

(defn pause-session-tx [player]
  (let [old-session (:player/session player)
        new-session (-> old-session
                        (assoc :mode :pause)
                        (assoc :start-time (:current-time old-session)))]
    [[:db/add (:db/id player) :player/session new-session]]))

(defn stop-session-tx [player]
  (let [old-session (:player/session player)
        session (-> old-session
                    (assoc :mode :stop)
                    (assoc :start-time 0)
                    (assoc :current-time (:total-time old-session)))]
    [[:db/add (:db/id player) :player/session session]]))

(defn seek-session-tx [player seek-time]
  (let [session (-> (:player/session player)
                    (assoc :mode :stop)
                    (assoc :start-time seek-time)
                    (assoc :current-time seek-time))]
    [[:db/add (:db/id player) :player/session session]]))


;; sub

(defn sub-player [system-conn id]
  @(p/pull system-conn '[*] id))

(defn sub-whole-player [system-conn player-id]
  @(p/pull system-conn '[{:player/current-ioframe [*]} *] player-id))

