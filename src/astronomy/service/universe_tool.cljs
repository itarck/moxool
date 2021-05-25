(ns astronomy.service.universe-tool
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]
   [astronomy.model.user.universe-tool :as m.universe-tool]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :universe-tool/load-object
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [object-id show?]} detail]
    (p/transact! conn [{:db/id object-id
                        :object/show? show?}])))


(defn init-service! [props {:keys [process-chan] :as env}]
  (println "universe tool started")
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))

