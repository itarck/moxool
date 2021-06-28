(ns astronomy.service.horizontal-coordinate-tool
  (:require
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :horizontal-coordinate-tool/log
  [props {:keys [conn]} {:event/keys [detail]}]
  (println detail))

(defmethod handle-event! :horizontal-coordinate-tool/change-show-longitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (:db/id tool)
             :horizontal-coordinate-tool/show-longitude? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :horizontal-coordinate-tool/change-show-latitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (:db/id tool)
             :horizontal-coordinate-tool/show-latitude? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :horizontal-coordinate-tool/change-show-compass
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (:db/id tool)
             :horizontal-coordinate-tool/show-compass? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :horizontal-coordinate-tool/change-show-horizontal-plane
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (:db/id tool)
             :horizontal-coordinate-tool/show-horizontal-plane? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :horizontal-coordinate-tool/change-radius
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool radius]} detail
        tx [{:db/id (:db/id tool)
             :horizontal-coordinate-tool/radius radius}]]
    (p/transact! conn tx)))

(defn init-service! [props {:keys [process-chan] :as env}]
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))


