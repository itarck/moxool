(ns astronomy.tools.constellation-tool.s
  (:require
   [posh.reagent :as p]
   [astronomy.objects.constellation.m :as m.constel]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :constellation-tool/log
  [props {:keys [conn]} {:event/keys [detail]}]
  (println detail))


(defmethod handle-event! :constellation-tool/change-query-type
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [constellation-tool new-query-type]} detail
        tx [{:db/id (:db/id constellation-tool)
             :constellation-tool/query-type new-query-type}]]
    (p/transact! conn tx)))

(defmethod handle-event! :constellation-tool/change-query-args
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [constellation-tool query-args]} detail
        tx [{:db/id (:db/id constellation-tool)
             :constellation-tool/query-args query-args}]]
    (p/transact! conn tx)))


(defmethod handle-event! :constellation-tool/show-lines
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [show? constellation-ids]} detail
        tx (mapcat (fn [id] (m.constel/update-show-lines-tx {:db/id id} show?)) constellation-ids)]
    (p/transact! conn tx)))

(defmethod handle-event! :constellation-tool/show-name
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [show? constellation-ids]} detail
        tx (mapcat (fn [id] (m.constel/update-show-name-tx {:db/id id} show?)) constellation-ids)]
    (p/transact! conn tx)))

(defn init-service! [props {:keys [process-chan] :as env}]
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))


