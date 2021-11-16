(ns astronomy.app.core
  (:require
   [applied-science.js-interop :as j]
   [reagent.dom :as rdom]
   [astronomy.system.mini2 :as mini2])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))


;; mount point

(def user-config
  {:astronomy/conn
   #:conn {:db-transit-str (read-resource "private/frame/solar-0.0.3.fra")}})

(def astronomy-system
  (mini2/create-system! user-config))


(defn update! []
  (rdom/render
   (:astronomy/root-view astronomy-system)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))



