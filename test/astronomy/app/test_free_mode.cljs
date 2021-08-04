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
   [astronomy.model.user.ruler-tool :as m.ruler-tool]))

;; 


;; real-time instance 

(def conn (:app/scene-conn free-app-instance))

(def service-chan (:app/service-chan free-app-instance))

(def dom-atom (get-in free-app-instance [:app/scene-system :system/dom-atom]))


(def ruler-tool @(p/pull conn '[*] [:tool/name "ruler-tool"]))

(:ruler-tool/status ruler-tool)

(->> (m.ruler-tool/change-status-tx ruler-tool :select1)
     (p/transact! conn))