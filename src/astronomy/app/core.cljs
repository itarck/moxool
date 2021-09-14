(ns astronomy.app.core
  (:require
   [applied-science.js-interop :as j]
   [reagent.dom :as rdom]
  ;;  [astronomy.app.simple-solar :as app.simple-solar]
   [astronomy.system.solar2 :as solar2])
  
  (:require-macros [methodology.lib.resource]))


;; mount point

#_(def app
  app.simple-solar/app-1)


(defn update! []
  (rdom/render
  ;;  (:system/view app)
   (:astronomy/root-view solar2/system)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))




(comment

  

  )
