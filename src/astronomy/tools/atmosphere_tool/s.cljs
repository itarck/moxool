(ns astronomy.tools.atmosphere-tool.s
  (:require
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :atmosphere-tool/log
  [props {:keys [conn]} {:event/keys [detail]}]
  (println detail))


(defmethod handle-event! :atmosphere-tool/change-show
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [atmosphere show?]} detail
        tx [{:db/id (:db/id atmosphere)
             :atmosphere/show? show?}]]
    (p/transact! conn tx)))


(defn init-service! [props {:keys [process-chan] :as env}]
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))


