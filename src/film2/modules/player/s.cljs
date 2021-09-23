(ns film2.modules.player.s
  (:require
   [cljs.core.async :as async :refer [go >! <! go-loop timeout chan]]
   [posh.reagent :as p]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [shu.calendar.timestamp :as timestamp]
   [film2.modules.ioframe.m :as ioframe.m]
   [film2.modules.iovideo.m :as iovideo.m]
   [film2.modules.player.m :as player]))


;; service 

(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :player/change-current-iovideo
  [_props {:keys [conn service-chan]} {:event/keys [detail]}]
  (let [{:keys [player iovideo]} detail
        tx [{:db/id (:db/id player)
             :player/current-iovideo (:db/id iovideo)}]]
    (p/transact! conn tx)
    (go (>! service-chan #:event{:action :player/load-current-iovideo
                                 :detail {:player player}}))))

(defmethod handle-event! :player/load-current-iovideo
  [_props {:keys [conn instance-atom]} {:event/keys [detail]}]
  (let [player-1 (d/pull @conn '[*] (get-in detail [:player :db/id]))
        iovideo-1 (d/pull @conn '[*] (get-in player-1 [:player/current-iovideo :db/id]))
        ioframe-1 (:iovideo/initial-ioframe iovideo-1)
        ioframe-system (ioframe.m/create-ioframe-system ioframe-1)]
    (swap! instance-atom assoc-in [:iovideo (:db/id iovideo-1)] ioframe-system)
    (p/transact! conn  [{:db/id (:db/id player-1)
                         :player/last-updated (js/Date.)}])
    (p/transact! conn (player/create-session-tx player-1 iovideo-1))))


(defn update-player-session!
  [player1 video1 system-conn scene-conn]
  (let [last-timestamp (get-in player1 [:player/session :current-timestamp])
        current-timestamp (timestamp/current-timestamp!)
        time1 (player/cal-current-time (:player/session player1) last-timestamp)
        time2 (player/cal-current-time (:player/session player1) current-timestamp)]
    (p/transact! scene-conn
                 (iovideo.m/get-tx-logs-in-range video1 time1 time2))
    (p/transact! system-conn
                 (player/update-session-tx player1 current-timestamp))))


(defmethod handle-event! :player/start-play
  [_props {:keys [conn instance-atom]} {:event/keys [detail]}]
  (go
    (let [player-id (get-in detail [:player :db/id])
          full-player (player/sub-whole-player conn player-id)
          video1 (d/pull @conn '[*] (get-in full-player [:player/current-iovideo :db/id]))
          scene-conn (get-in @instance-atom [:iovideo (:db/id video1) :ioframe-system/conn])]
      (when-not (player/check-session-starting? full-player)
        (p/transact! conn (player/start-session-tx full-player (timestamp/current-timestamp!)))
        (loop []
          (<! (timeout 20))
          (let [system-db @conn
                player1 (d/pull system-db '[*] player-id)]
            (cond
              (player/should-session-stop? player1 video1)
              (do
                (p/transact! conn (player/stop-session-tx player1))
                :stop)

              (player/check-session-paused? player1) :pause

              (player/check-session-starting? player1)
              (do (update-player-session! player1 video1 conn scene-conn)
                  (recur))
              :else (println "player error!!!! "))))))))


(defmethod handle-event! :player/pause-play
  [_props {:keys [conn]} {:event/keys [detail]}]
  (let [player-id (get-in detail [:player :db/id])
        player1 (d/pull @conn '[*] player-id)]
    (p/transact! conn (player/pause-session-tx player1))))


#_(defmethod handle-event! :player/seek-play
  [{:keys [player-id seek-time]} {:keys [system-conn scene-conn meta-chan]} event]
  (let [player1 (player/pull-whole @system-conn player-id)
        video1 (:player/current-video player1)]
    (p/transact! system-conn (player/seek-session-tx player1 seek-time))
    ;; (d/reset-conn! scene-conn (dt/read-transit-str (:video/initial-db-transit video1)))
    (p/transact! scene-conn
                 (iovideo.m/get-tx-logs-in-range video1 0 seek-time))))


(defn init-service! [props {:keys [process-chan] :as env}]
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (cond
          (vector? event) (go-loop [[e & rs] event]
                            (when (seq e)
                              (let [_rst (<! (handle-event! props env e))]
                                (recur rs))))
          :else (handle-event! props env event))
        (catch js/Error e
          (println "player service error: " e))))
    (recur)))




(comment 
  
  )


