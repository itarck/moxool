(ns astronomy.app.core
  (:require
   [integrant.core :as ig]
   [applied-science.js-interop :as j]
   [reagent.dom :as rdom]
   [astronomy.system.mini2 :as mini2]
   [film2.system.studio :as studio]
   ))


;; mount point

#_(def astronomy-system 
  (ig/init mini2/config))

(defonce studio=system 
  (studio/create-app! {}))


(defn update! []
  (rdom/render
  ;;  (:astronomy/root-view astronomy-system)
   (:studio/view studio=system)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))



