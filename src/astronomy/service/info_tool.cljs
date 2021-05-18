(ns astronomy.service.info-tool
  (:require
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))


(defn init-service! [props {:keys [process-chan service-chan conn]}]
  (let [{:keys [user]} props]
    (println "info-control started")
    (go-loop []
      (let [{:event/keys [action detail] :as event} (<! process-chan)
            {:keys [current-tool]} detail]
        ;; (println event)
        (try
          (case action

            :info-tool/object-clicked
            (let [{:keys [object]} detail]
              (p/transact! conn [[:db/add (:db/id current-tool) :info-tool/object (:db/id object)]])))


          (catch js/Error e
            (js/console.log e))))
      (recur))))