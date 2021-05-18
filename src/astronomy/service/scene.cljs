(ns astronomy.service.scene
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))



(defn init-scene-service! [props {:keys [scene-atom scene-chan conn service-chan]}]
  (println "scene service started")
  (go-loop []
    (let [{:event/keys [action detail] :as event} (<! scene-chan)]
      (println event)
      (try
        (case action
          :scene/change-to-free-mode (go (>! service-chan #:event {:action :spaceship-camera-control/reset})
                                         (swap! scene-atom assoc :mode :read-and-write))
          :scene/change-to-play-mode (go (swap! scene-atom assoc :mode :read-only))
          (println "not match!!!" event))
        (catch js/Error e
          (js/console.log e))))
    (recur)))