(ns astronomy.app.core
  (:require
   [applied-science.js-interop :as j]
   [reagent.dom :as rdom]
   [astronomy.app.simple-solar :as app.simple-solar])
  (:require-macros [methodology.lib.resource]))


;; mount point

(def app
  app.simple-solar/app-1)


(defn update! []
  (rdom/render
   (:system/view app)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))




(comment

  

  )
