(ns astronomy.app.core
  (:require
   [applied-science.js-interop :as j]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [reagent.dom :as rdom]
   [methodology.lib.chest :as chest]
   [astronomy.app.scene-free :as scene-free]
   [astronomy.app.scene-in-editor :as scene-in-editor]
   [astronomy.app.scene-in-player :as scene-in-player]
   ))



;; mount point

(defonce editor-app-instance (scene-in-editor/create-app!
                          #:app {:name "astronomy"}))


#_(defonce player-app-instance (scene-in-player/create-app!
                          #:app {:name "astronomy"
                                 :db-url "/edn/store-system-conn1.edn"}))

(defonce free-app-instance (scene-free/create-app! {}))


(defn update! []
  (rdom/render
   (:app/view editor-app-instance)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))




(comment

  (def system-conn (get-in editor-app-instance [:app/editor-system :system/conn]))
  (def scene-conn (get-in editor-app-instance [:app/scene-system :system/conn]))

  (count (d/datoms @scene-conn :eavt))


  (chest/pull-one @system-conn [:player/name "default"])

  (def db (->
           (:video/initial-db-str (chest/pull-one @system-conn 5))
           dt/read-transit-str))

  (count (d/datoms db :eavt))

  (def conn (d/create-conn {}))

  (d/reset-conn! conn db)
  (d/reset-conn! scene-conn db)

  (count (d/datoms @conn :eavt))
  (count (d/datoms db :eavt))
  ;; 
  )