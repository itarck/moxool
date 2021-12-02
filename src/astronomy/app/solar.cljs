(ns astronomy.app.solar
  (:require
   [applied-science.js-interop :as j]
   [reagent.dom :as rdom]
   [astronomy.system.solar :as solar])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))


;; mount point

(def user-config
  {:astronomy/conn {:db-transit-str (read-resource "private/frame/dev-20211202-1753.fra")}})


(defonce system
  (solar/create-system! user-config))


(defn update! []
  (rdom/render
   (:astronomy/root-view system)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))



