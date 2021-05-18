(ns astronomy.app.test-editor-app
  (:require
   [applied-science.js-interop :as j]
   [datascript.core :as d]
   [posh.reagent :as p]
   [film.model.video :as m.video]
   [methodology.lib.chest :as chest]
   [film.model.editor :as m.editor]
   [astronomy.app.scene-in-editor :refer [create-app!]]))



(def app-instance (create-app! #:app {}))


(def scene-conn
  (get-in app-instance [:app/scene-system :system/conn]))

(def system-conn
  (get-in app-instance [:app/editor-system :system/conn]))


(def editor-id [:editor/name "default"])
(def player-id [:player/name "default"])


(chest/pull-one @system-conn player-id)
;; => {:db/id 3, :player/current-video #:db{:id 4}, :player/name "default"}


(m.editor/create-video! system-conn editor-id)

(m.editor/start-record! system-conn scene-conn editor-id)


