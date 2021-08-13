(ns astronomy.app.test-free-mode
  (:require
   [applied-science.js-interop :as j]
   [datascript.core :as d]
   [posh.reagent :as p]
   [cljs.core.async :refer [go >! timeout <!]]
   [astronomy.app.scene-free :as scene-free]
   [astronomy.app.core :refer [free-app-instance]]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.clock :as m.clock]
   [astronomy.model.astronomical-point :as m.apt]
   [astronomy.model.user.ruler-tool :as m.ruler-tool]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   [astronomy.model.constellation :as m.constel]))

;; 


;; real-time instance 

(def conn (:app/scene-conn free-app-instance))

(def service-chan (:app/service-chan free-app-instance))

(def dom-atom (get-in free-app-instance [:app/scene-system :system/dom-atom]))


(def ruler-tool @(p/pull conn '[*] [:tool/name "ruler-tool"]))

(m.apt/find-all-ids @conn)
;; => [9271 9272 9273]

(def apt-1 (d/pull @conn '[*] 9272))


(let [tx [[:db/add (:db/id apt-1) :astronomical-point/size 1.01]]]
  (p/transact! conn tx))

conn

(def scc @(p/pull conn '[*] [:spaceship-camera-control/name "default"]))

(let [tx (m.spaceship/set-min-distance-tx scc 250)]
  (p/transact! conn tx))


(def star-1
  @(p/pull conn '[*] [:star/HR 2491]))

(def terr-1
  @(p/pull conn '[*] [:coordinate/name "地球坐标系"]))

terr-1

(let [tx [{:db/id (:db/id terr-1)
           :terrestrial-coordinate/radius 10000}]]
  (p/transact! conn tx))


(let [event #:event{:action :clock-tool/set-time-in-days
                    :detail {:clock {:db/id [:clock/name "default"]}
                             :time-in-days (* 365 -2400)}}]
  (go (>! service-chan event)))