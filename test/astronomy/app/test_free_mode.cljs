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
   [astronomy.component.mouse :as c.mouse]))

;; 


;; real-time instance 

(def conn (:app/scene-conn free-app-instance))

(def service-chan (:app/service-chan free-app-instance))

(def dom-atom (get-in free-app-instance [:app/scene-system :system/dom-atom]))


(def scene @(p/pull conn '[*] [:scene/name "solar"]))
(def clock @(p/pull conn '[*] [:clock/name "default"]))


(def camera (:camera @dom-atom))

(c.mouse/to-world-vector3 (:three-instance @dom-atom) [0.5 0.5])

(j/get camera :projectionMatrix)

(def three-object (:three-instance @dom-atom))

three-object

(c.mouse/get-mouse-direction-vector3 three-object)

(c.mouse/get-camera-object2 three-object)

(c.mouse/get-normalized-mouse three-object)