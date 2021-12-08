(ns astronomy.app.test-solar
  (:require
   [astronomy.app.solar :as app.solar]
   [astronomy.system.solar :as system.solar]
   [astronomy.lib.api :as api]
   [cljs.core.async :refer [go >!]]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [integrant.core :as ig]
   [fancoil.core :as fancoil]
   [astronomy.space.backpack.m :as backpack.m]
   [astronomy.scripts.angel.base-db :as base-db])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))


(def conn
  (:astronomy/conn app.solar/system))


(api/save-db-file @conn "/private/frame/angel/scene-1-2-v2.fra")

(def user-config
  app.solar/user-config)

(def config
  (fancoil/merge-config system.solar/default-config user-config))


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

(defn init-tool! [conn]
  (let [tools [{:db/id [:tool/name "ppt tool"]}
               {:db/id [:tool/name "clock control 1"]}
               {:db/id [:tool/name "planet-tool"]}
               {:db/id [:tool/name "satellite-tool"]}
               {:db/id [:tool/name "spaceship camera tool"]}
               {:db/id [:tool/name "horizon-coordinate-tool"]}
               {:db/id [:tool/name "terrestrial-coordinate-tool"]}
               {:db/id [:tool/name "astronomical-coordinate-tool"]}
               {:db/id [:tool/name "atmosphere-tool"]}
               {:db/id [:tool/name "astronomical-point-tool"]}
               {:db/id [:tool/name "ellipse-orbit-tool"]}]
        person (d/pull @conn '[*] [:user/name "dr who"])
        backpack (d/pull @conn '[*] (-> person :user/backpack :db/id))]
    (p/transact! conn (backpack.m/put-in-backpack-tx backpack tools))))

(defn init-scene-1-1
  [])

(init-tool! conn)

(base-db/clear-backpacks! conn)



