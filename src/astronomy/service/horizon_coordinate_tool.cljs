(ns astronomy.service.horizon-coordinate-tool
  (:require
   [posh.reagent :as p]
   [datascript.core :as d]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.horizon-coordinate :as m.horizon]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :horizon-coordinate/log
  [props {:keys [conn]} {:event/keys [detail]}]
  (println "horizontal-cooridnate-tool/log: " detail))

(defmethod handle-event! :horizon-coordinate/change-show-longitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [horizon-coordinate show?]} detail]
    (p/transact! conn (m.horizon/set-longitude-tx horizon-coordinate show?))))

(defmethod handle-event! :horizon-coordinate/change-show-latitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [horizon-coordinate show?]} detail]
    (p/transact! conn (m.horizon/set-latitude-tx horizon-coordinate show?))))

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

(defmethod handle-event! :horizon-coordinate/set-scene-reference
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [horizon-coordinate]} detail
        astro-scene (get-in props [:astro-scene])]
    (p/transact! conn (m.astro-scene/set-scene-coordinate-tx astro-scene horizon-coordinate))))

#_(defmethod handle-event! :horizon-coordinate/change-query-args
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool query-args]} detail
        chinese-name (first query-args)]
    (p/transact! conn (m.horizon-tool/change-query-args-tx tool query-args))
    (when-not (= chinese-name "未选择")
      (let [tx (m.horizon-tool/change-target-tx tool {:db/id [:horizontal-coordinate/chinese-name chinese-name]})]
        ;; (println :horizon-coordinate/change-query-args tx)
        (p/transact! conn tx)))))


(defn init-service! [props {:keys [process-chan] :as env}]
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))


