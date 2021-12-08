(ns astronomy.app.solar
  (:require
   [applied-science.js-interop :as j]
   [datascript.transit :as dt]
   [reagent.dom :as rdom]
   [astronomy.system.solar :as solar])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))


;; mount point

(def db-url 
  {:base "frame/dev/base-v1.fra"
   :scene-1-1 "frame/dev/scene-1-1-v1.fra"
   :scene-1-2 "frame/dev/scene-1-2-v1.fra"
   :scene-1-3 "frame/dev/scene-1-3-v2.fra"})


(def db
  (->>
   (read-resource "frame/dev/scene-1-3-v2.fra")
   (dt/read-transit-str)))


(def user-config
  {:astronomy/conn {:initial-db db}})


(defonce system
  (solar/create-system! user-config))


(defn update! []
  (rdom/render
   (:astronomy/root-view system)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))



