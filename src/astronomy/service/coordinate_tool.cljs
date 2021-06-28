(ns astronomy.service.coordinate-tool
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]
   [astronomy.model.coordinate :as m.coordinate]
   [astronomy.model.user.coordinate-tool :as m.coordinate-tool]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :coordinate-tool/set-track-position
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [coordinate-id track-position-id]} detail]
    (p/transact! conn (m.coordinate/update-track-position-tx coordinate-id track-position-id))
    (p/transact! conn (m.coordinate/update-coordinate-tx @conn coordinate-id))))

(defmethod handle-event! :coordinate-tool/set-track-rotation
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [coordinate-id track-rotation-id]} detail]
    (p/transact! conn (m.coordinate/update-track-rotation-tx coordinate-id track-rotation-id))
    (p/transact! conn (m.coordinate/update-coordinate-tx @conn coordinate-id))))



(defn init-service! [props {:keys [process-chan] :as env}]
  (println "coordinate-control started")
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))

