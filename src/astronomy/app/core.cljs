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
   [astronomy.app.free-room :as app.free-room])
  (:require-macros [methodology.lib.resource]))



;; mount point

#_(defonce editor-app-instance (scene-in-editor/create-app!
                              #:app {:name "astronomy"}))


#_(defonce player-app-instance (scene-in-player/create-app!
                              #:app {:name "astronomy"
                                     :db-url "/db/10-sphere-v2.edn"}))

#_(defonce free-app-instance (scene-free/create-app! #:app{:scene-db-url "/temp/free-mode.edn"}))

(defonce free-room (app.free-room/create-app! {}))


(defn update! []
  (rdom/render
   (:free-room/view free-room)
   (j/call js/document :getElementById  "app")))


(defn ^:export init! []
  (update!))




(comment

  free-room

  )
