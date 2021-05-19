(ns astronomy.service.meta
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))


(defmulti handle-event! (fn [event env] (:event/action event)))


(defmethod handle-event! :meta/change-to-free-mode
  [event {:keys [meta-atom service-chan]}]
  (go (>! service-chan #:event {:action :spaceship-camera-control/reset})
      (swap! meta-atom assoc :mode :read-and-write)))


(defmethod handle-event! :meta/change-to-play-mode
  [event {:keys [meta-atom]}]
  (go (swap! meta-atom assoc :mode :read-only)))


(defn init-meta-service! [props {:keys [meta-atom meta-chan conn service-chan]}]
  (println "scene service started")
  (let [process-env {:meta-atom meta-atom
                     :meta-chan meta-chan
                     :conn conn
                     :service-chan service-chan}]
    (go-loop []
      (let [{:event/keys [action detail] :as event} (<! meta-chan)
            process-event (merge event props)]
        (try
          (handle-event! process-event process-env)
          (catch js/Error e
            (js/console.log "meta-service" e))))
      (recur)))
  )