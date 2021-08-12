(ns astronomy.service.horizon-coordinate-tool
  (:require
   [posh.reagent :as p]
   [astronomy.model.horizon-coordinate :as m.horizon]
   [astronomy.model.user.horizon-coordinate-tool :as m.horizon-coordinate-tool]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :horizon-coordinate/log
  [props {:keys [conn]} {:event/keys [detail]}]
  (println "horizontal-cooridnate-tool/log: " detail))

(defmethod handle-event! :horizon-coordinate/change-show-longitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [horizon-coordinate show?]} detail]
    (p/transact! conn (m.horizon/change-show-longitude-tx horizon-coordinate show?))))

(defmethod handle-event! :horizon-coordinate/change-show-latitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [horizon-coordinate show?]} detail]
    (p/transact! conn (m.horizon/change-show-latitude-tx horizon-coordinate show?))))

(defmethod handle-event! :horizon-coordinate/change-show-compass
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [horizon-coordinate show?]} detail]
    (p/transact! conn (m.horizon/change-show-compass-tx horizon-coordinate show?))))

(defmethod handle-event! :horizon-coordinate/change-show-horizontal-plane
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [horizon-coordinate show?]} detail]
    (p/transact! conn (m.horizon/change-show-horizontal-plane-tx horizon-coordinate show?))))

(defmethod handle-event! :horizon-coordinate/change-radius
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [horizon-coordinate radius]} detail]
    (p/transact! conn (m.horizon/change-radius-tx horizon-coordinate radius))))

(defmethod handle-event! :horizon-coordinate/change-query-args
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool query-args]} detail
        tx (m.horizon-coordinate-tool/update-query-args-tx @conn tool query-args)]
    (p/transact! conn tx)))


(defn init-service! [props {:keys [process-chan] :as env}]
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))


