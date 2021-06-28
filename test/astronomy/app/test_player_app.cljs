(ns astronomy.app.test-player-app
  (:require
   [applied-science.js-interop :as j]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [film.model.video :as m.video]
   [methodology.lib.chest :as chest]
   [film.model.editor :as m.editor]
   [astronomy.app.core :refer [player-app-instance]]
   [astronomy.app.scene-in-player :refer [create-app!]]))



#_(def player-app-instance (create-app!
                              #:app {:name "astronomy"
                                     :db-url "/db/store-system-conn-2.edn"}))

(def scene-conn
  (get-in player-app-instance [:app/scene-system :system/conn]))

(def system-conn
  (get-in player-app-instance [:app/player-system :system/conn]))

(keys (:app/player-system player-app-instance))

(def editor-id [:editor/name "default"])
(def player-id [:player/name "default"])


(d/pull @system-conn '[*] player-id)


(def video-ids (d/q m.video/find-all-video-ids-query @system-conn))
video-ids

(def video1 (d/pull @system-conn '[*] (second video-ids)))

(keys (d/pull @system-conn '[*] (second video-ids)))
;; => (:video/initial-db-str :video/total-time :video/start-timestamp :video/name :db/id :video/stop-timestamp :video/scene :video/tx-logs :video/initial-db-transit)


(def conn1 (d/create-conn {}))

(def db1 (dt/read-transit-str (:video/initial-db-transit video1)))

(d/schema db1)

(d/reset-conn! conn1 db1)

(count (d/datoms @conn1 :eavt))

conn1