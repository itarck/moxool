(ns film.service.player
  (:require
   [cljs.core.async :as async :refer [go >! <! go-loop timeout chan]]
   [posh.reagent :as p]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [shu.general.time :as time]
   [methodology.lib.chest :as chest]
   [film.model.player :as player]
   [film.model.video :as video]))


;; service 

(defmulti handle-event! (fn [action _event _env] action))


(defmethod handle-event! :player/open-video
  [_action {:keys [player-id video-id]} {:keys [system-conn]}]
  (let [player1 (chest/pull-one @system-conn player-id)
        video1 (chest/pull-one @system-conn video-id)
        tx (concat
            [[:db/add player-id :player/current-video video-id]]
            (player/create-session-tx player1 video1))]
    (p/transact! system-conn tx)))


(defn update-player-session!
  [player1 video1 system-conn scene-conn]
  (let [last-timestamp (get-in player1 [:player/session :current-timestamp])
        current-timestamp (time/get-timestamp)
        time1 (player/cal-current-time (:player/session player1) last-timestamp)
        time2 (player/cal-current-time (:player/session player1) current-timestamp)]
    (p/transact! scene-conn
                 (video/get-tx-logs-in-range video1 time1 time2))
    (p/transact! system-conn
                 (player/update-session-tx player1 current-timestamp))))


(defmethod handle-event! :player/start-play
  [_action {:keys [player-id]} {:keys [system-conn scene-conn meta-chan]}]
  (go
    (let [full-player (player/sub-whole-player system-conn player-id)
          video1 (:player/current-video full-player)]
      (when-not (player/check-session-starting? full-player)
        (>! meta-chan #:event{:action :meta/change-to-play-mode})
        (p/transact! system-conn (player/start-session-tx full-player (time/get-timestamp)))
        (loop []
          (<! (timeout 20))
          (let [system-db @system-conn
                player1 (d/pull system-db '[*] player-id)]
            (cond
              (player/should-session-stop? player1 video1)
              (do
                (p/transact! system-conn (player/stop-session-tx player1))
                (>! meta-chan #:event{:action :meta/change-to-free-mode})
                :stop)

              (player/check-session-paused? player1) :pause

              (player/check-session-starting? player1)
              (do (update-player-session! player1 video1 system-conn scene-conn)
                  (recur))
              :else (println "player error!!!! "))))))))


(defmethod handle-event! :player/pause-play
  [_ {:keys [player-id]} {:keys [system-conn meta-chan]}]
  (let [player1 (d/pull @system-conn '[*] player-id)]
    (p/transact! system-conn (player/pause-session-tx player1))
    (go (>! meta-chan #:event{:action :meta/change-to-free-mode}))))


(defmethod handle-event! :player/seek-play
  [_ {:keys [player-id seek-time]} {:keys [system-conn scene-conn meta-chan]}]
  (let [player1 (player/pull-whole @system-conn player-id)
        video1 (:player/current-video player1)]
    (p/transact! system-conn (player/seek-session-tx player1 seek-time))
    ;; (d/reset-conn! scene-conn (dt/read-transit-str (:video/initial-db-str video1)))
    (p/transact! scene-conn
                 (video/get-tx-logs-in-range video1 0 seek-time))
    (go (>! meta-chan #:event{:action :meta/change-to-free-mode}))))


(defn init-service! [props env]
  (let [{:keys [in-chan system-conn scene-system]} env
        process-props {:player-id (get-in props [:player :db/id])}
        process-env {:system-conn system-conn
                     :meta-chan (:system/meta-chan scene-system)
                     :scene-conn (:system/conn scene-system)}]
    (go-loop []
      (let [[_service signal event] (<! in-chan)
            hprops (merge process-props event)]
        (handle-event! signal hprops process-env))
      (recur))))




(comment 
  
  )
