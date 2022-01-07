(ns astronomy.app.test-solar
  (:require
   [astronomy.app.solar :as app.solar]
   [astronomy.system.solar :as system.solar]
   [astronomy.lib.api :as api]
   [cljs.core.async :refer [go >!]]
   [datascript.core :as d]
   [posh.reagent :as p]
   [integrant.core :as ig]
   [fan.core :as fan]
   [astronomy.scripts.angel.lib :as slib])
  )


(def conn
  (:astronomy/conn app.solar/system))


(api/save-db-file @conn "/private/frame/angel/scene-1-2-v2.fra")

(def user-config
  app.solar/user-config)

(def config
  (fan/merge-config system.solar/default-config user-config))


(def system2
  (ig/init config))

(keys system2)

(ig/halt! system2)


(def service-chan
  (:astronomy/service-chan app.solar/system))


(go (>! service-chan #:event{:action :spaceship-camera-control/refresh-camera
                             :detail {:spaceship-camera-control {:db/id [:spaceship-camera-control/name "default"]}}}))


(:spaceship-camera-control/position (d/pull @conn '[*] [:spaceship-camera-control/name "default"]))

(defn dispatch
  [event]
  (go (>! service-chan event)))

(def event
  #:event {:action :clock-tool/set-time-in-days
           :detail {:clock {:db/id [:clock/name "default"]}
                    :time-in-days 10000}})

(dispatch event)


(slib/clear-backpack! conn)

(slib/init-tool! conn slib/all-tools)



