(ns astronomy.service.ppt-tool
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]
   [astronomy.tools.ppt-tool.m :as m.ppt-tool]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :ppt-tool/log
  [props {:keys [conn]} {:event/keys [detail]}]
  (println detail))

(defmethod handle-event! :ppt-tool/next-page
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [current-ppt]} detail
        tx (m.ppt-tool/next-page-tx current-ppt)]
    (p/transact! conn tx)))

(defmethod handle-event! :ppt-tool/prev-page
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [current-ppt]} detail
        tx (m.ppt-tool/prev-page-tx current-ppt)]
    (p/transact! conn tx)))

(defmethod handle-event! :ppt-tool/change-query-args
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [ppt-tool query-args]} detail
        tx [{:db/id (:db/id ppt-tool)
             :ppt-tool/query-args query-args}]]
    (p/transact! conn tx)))


(defn init-service! [props {:keys [process-chan] :as env}]
  (println "ppt tool started")
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))

