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

;; help functions


(defn dispatch
  [event]
  (go (>! service-chan event)))


(defn re-frash-camera! []
  (go (>! service-chan 
          #:event{:action :spaceship-camera-control/refresh-camera
                  :detail {:spaceship-camera-control {:db/id [:spaceship-camera-control/name "default"]}}})))

;; processes


(comment ;; scene 1-1 

  (slib/init-tool! conn slib/all-tools)
  
  (let [tools-1-1 [{:db/id [:tool/name "clock control 1"]}
                   {:db/id [:tool/name "planet-tool"]}]]
    (slib/init-tool! conn tools-1-1))

  (re-frash-camera!)
  
  (let [db-url "/frame/dev/scene-1-1-v1.fra"]
    (api/save-db-file @conn db-url))
;;
  )


(comment  ;; scene 1-2

  (slib/init-tool! conn slib/all-tools)

  (let [tools-1-2 [{:db/id [:tool/name "clock control 1"]}
                   {:db/id [:tool/name "planet-tool"]}
                   {:db/id [:tool/name "constellation-tool"]}]]
    (slib/init-tool! conn tools-1-2))

  (re-frash-camera!)

  (let [db-url "/frame/dev/scene-1-2-v1.fra"]
    (api/save-db-file @conn db-url))

;;
  )


(comment  ;; scene 1-3

  (slib/init-tool! conn slib/all-tools)

  (let [tools-1-3 [{:db/id [:tool/name "clock control 1"]}
                   {:db/id [:tool/name "planet-tool"]}
                   {:db/id [:tool/name "constellation-tool"]}
                   {:db/id [:tool/name "astronomical-coordinate-tool"]}
                   {:db/id [:tool/name "terrestrial-coordinate-tool"]}
                   {:db/id [:tool/name "horizon-coordinate-tool"]}]]
    (slib/init-tool! conn tools-1-3))

  (re-frash-camera!)

  (let [db-url "/frame/dev/scene-1-3-v1.fra"]
    (api/save-db-file @conn db-url))

;;
  )