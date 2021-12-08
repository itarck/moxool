(ns astronomy.scripts.angel.repl
  (:require
   [astronomy.app.solar :as app.solar]
   [astronomy.lib.api :as api]
   [cljs.core.async :refer [go >!]]
   [astronomy.scripts.angel.lib :as slib]))


;; value and refs 

(def conn
  (:astronomy/conn app.solar/system))

(def service-chan
  (:astronomy/service-chan app.solar/system))

(def re-fresh-camera-event
  #:event{:action :spaceship-camera-control/refresh-camera
          :detail {:spaceship-camera-control {:db/id [:spaceship-camera-control/name "default"]}}})

(def db-url "/private/frame/angel/scene-1-2-v2.fra")

;; help functions


(defn dispatch
  [event]
  (go (>! service-chan event)))


;; processes

;; scene 1-1 

(comment

  (api/save-db-file @conn db-url)

  (slib/init-tool! conn slib/all-tools)

  (slib/clear-backpack! conn)

;;
  )
