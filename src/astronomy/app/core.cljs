(ns astronomy.app.core
  (:require
   [integrant.core :as ig]
   [applied-science.js-interop :as j]
   [reagent.dom :as rdom]
   [astronomy.system.mini2 :as mini2]))


;; mount point

(def astronomy-system
  (ig/init mini2/config))


(defn update! []
  (rdom/render
   (:astronomy/root-view astronomy-system)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))



