(ns astronomy.app.solar
  (:require
   [applied-science.js-interop :as j]
   [reagent.dom :as rdom]
   [astronomy.system.solar :as solar]
   [astronomy.scripts.angel.scene-1-1 :as scene-1-1])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))


;; mount point

(def db-transit-str 
  (read-resource "private/frame/dev-20211202-1753.fra"))

(def db
  (scene-1-1/create-db))

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



