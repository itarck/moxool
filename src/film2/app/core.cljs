(ns film2.app.core
  (:require
   [applied-science.js-interop :as j]
   [reagent.dom :as rdom]
   [film2.system.studio :as studio]))


;; mount point

(def system (studio/create-app! {}))


(defn update! []
  (rdom/render
   (:studio/view system)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))



(comment
  
  (keys system)
  
  )