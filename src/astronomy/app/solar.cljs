(ns astronomy.app.solar
  (:require
   [applied-science.js-interop :as j]
   [datascript.transit :as dt]
   [reagent.dom :as rdom]
   [astronomy.system.solar :as solar])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))


;; mount point

#_(def db
  (scene/create-db))

(def db
  (->>
   (read-resource "frame/dev/dev-20211206-1.fra")
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



