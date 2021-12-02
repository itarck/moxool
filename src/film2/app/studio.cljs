(ns film2.app.studio
  (:require
   [applied-science.js-interop :as j]
   [reagent.dom :as rdom]
   [film2.system.studio :as studio]
   [film2.db.studio :as db.studio]))


;; mount point

(def user-config
  #:studio {:conn {:initial-db db.studio/simple-db}})


(def system
  (studio/create-app! user-config))


(defn update! []
  (rdom/render
   (:studio/view system)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))



(comment
  
  (keys system)
  
  )