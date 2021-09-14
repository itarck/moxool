(ns astronomy.tools.horizon-coordinate-tool.s
  (:require
   [posh.reagent :as p]
   [astronomy.objects.horizon-coordinate.m :as horizon.m]
   [astronomy.tools.horizon-coordinate-tool.m :as horizon.m-coordinate-tool]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :horizon-coordinate/log
  [props {:keys [conn]} {:event/keys [detail]}]
  (println "horizontal-cooridnate-tool/log: " detail))

(defmethod handle-event! :horizon-coordinate/change-show-longitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [horizon-coordinate show?]} detail]
    (p/transact! conn (horizon.m/change-show-longitude-tx horizon-coordinate show?))))

(defmethod handle-event! :horizon-coordinate/change-show-latitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [horizon-coordinate show?]} detail]
    (p/transact! conn (horizon.m/change-show-latitude-tx horizon-coordinate show?))))

(defmethod handle-event! :horizon-coordinate/change-show-compass
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [horizon-coordinate show?]} detail]
    (p/transact! conn (horizon.m/change-show-compass-tx horizon-coordinate show?))))

(defmethod handle-event! :horizon-coordinate/change-show-horizontal-plane
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [horizon-coordinate show?]} detail]
    (p/transact! conn (horizon.m/change-show-horizontal-plane-tx horizon-coordinate show?))))

(defmethod handle-event! :horizon-coordinate/change-radius
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [horizon-coordinate radius]} detail]
    (p/transact! conn (horizon.m/change-radius-tx horizon-coordinate radius))))

(defmethod handle-event! :horizon-coordinate/change-query-args
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool query-args]} detail
        tx (horizon.m-coordinate-tool/update-query-args-tx @conn tool query-args)]
    (p/transact! conn tx)))


(defn init-service! [props {:keys [process-chan] :as env}]
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))


