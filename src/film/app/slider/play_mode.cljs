(ns film.app.slider.play-mode
  (:require
   [applied-science.js-interop :as j]
   [integrant.core :as ig]
   [reagent.dom :as dom]
   [methodology.lib.circuit :as circuit]
   [film.app.slider.free-mode :refer [create-scene-system]]
   [film.ig.player :refer [create-player-system]]))


;; model 


(def scene-initial-tx [{:slider/name "bmi"
                        :slider/value 50}])


(derive :app/scene-system :circuit/system)
(derive :app/player-system :circuit/system)
(derive :app/meta-atom :circuit/ratom)


(def app-config
  #:app {:meta-atom #:ratom {:init-value {:status :read-and-write}}
         :scene-system #:system{:system-fn create-scene-system
                                :props {:initial-tx scene-initial-tx}
                                :env {:meta-atom (ig/ref :app/meta-atom)}}
         :player-system #:system {:system-fn create-player-system
                                  :env {:meta-atom (ig/ref :app/meta-atom)
                                        :scene-system (ig/ref :app/scene-system)}}})

(def app-instance (ig/init app-config))

;; mount point

(defn update! []
  (dom/render
   (get-in app-instance [:app/player-system :system/view])
   (j/call js/document :getElementById  "app")))

(defn ^:export init! []
  (update!))

