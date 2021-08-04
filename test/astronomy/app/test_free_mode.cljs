(ns astronomy.app.test-free-mode
  (:require
   [applied-science.js-interop :as j]
   [datascript.core :as d]
   [posh.reagent :as p]
   [cljs.core.async :refer [go >! timeout <!]]
   [astronomy.app.scene-free :as scene-free]
   [astronomy.app.core :refer [free-app-instance]]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.clock :as m.clock]))

;; 


;; real-time instance 

(def conn (:app/scene-conn free-app-instance))

(def service-chan (:app/service-chan free-app-instance))

(def dom-atom (get-in free-app-instance [:app/scene-system :system/dom-atom]))


(def scene @(p/pull conn '[*] [:scene/name "solar"]))
(def clock @(p/pull conn '[*] [:clock/name "default"]))

clock

(->> (m.clock/set-clock-time-in-days-tx (:db/id clock) 10000)
     (p/transact! conn))


(->> (m.astro-scene/refresh-tx @conn scene)
     (p/transact! conn))