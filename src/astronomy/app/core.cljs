(ns astronomy.app.core
  (:require
   [applied-science.js-interop :as j]
   [reagent.dom :as rdom]
  ;;  [film2.system.studio :as studio]
   [astronomy.app.simple-solar :as app.simple-solar])
  (:require-macros [methodology.lib.resource]))



;; mount point


;; (defonce studio (studio/create-app! {}))


(defn update! []
  (rdom/render
  ;;  (:studio/view studio)
   (:system/view app.simple-solar/app-1)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))




(comment

  

  )
