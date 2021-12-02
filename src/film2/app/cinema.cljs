(ns film2.app.cinema
  (:require
   [applied-science.js-interop :as j]
   [reagent.dom :as rdom]
   [film2.system.cinema :as cinema]
   [film2.db.cinema :as db.cinema]))


;; mount point

(def user-config
  #:cinema {:conn {:initial-db db.cinema/simple-db}})


(def system
  (cinema/create-app! user-config))


(defn update! []
  (rdom/render
   (:cinema/view system)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))



(comment

  (keys system)
  )