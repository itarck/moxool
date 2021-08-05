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
   [astronomy.model.user.ruler-tool :as m.ruler-tool]))

;; 


;; real-time instance 

(def conn (:app/scene-conn free-app-instance))

(def service-chan (:app/service-chan free-app-instance))

(def dom-atom (get-in free-app-instance [:app/scene-system :system/dom-atom]))


(def ruler-tool @(p/pull conn '[*] [:tool/name "ruler-tool"]))

(m.apt/find-all-ids @conn)
;; => [9271 9272 9273]

(def apt-1 (d/pull @conn '[*] 9273))


(let [tx [[:db/add (:db/id apt-1) :astronomical-point/size 10]]]
  (p/transact! conn tx))

