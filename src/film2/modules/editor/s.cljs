(ns film2.modules.editor.s
  (:require
   [cljs.core.async :refer [go go-loop >! <!]]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [cljs-http.client :as http]
   [methodology.lib.circuit]
   [astronomy.system.solar :as system.solar]))


(defmulti handle-event! (fn [_ _ event] (:event/action event)))

(defmethod handle-event! :editor/pull-current-frame
  [{:keys [editor]} {:keys [conn]} event]
  (let [editor-1 (d/pull @conn '[*] (:db/id editor))
        frame-1 (d/pull @conn '[*] (get-in editor-1 [:editor/current-frame :db/id]))
        db-url (get-in frame-1 [:frame/name])]
    (go (let [response (<! (http/get db-url))
              stored-data (:body response)]
          (p/transact! conn [{:db/id (:db/id frame-1)
                              :frame/db-string stored-data}])))))

(defmethod handle-event! :editor/load-current-frame
  [{:keys [editor]} {:keys [conn instance-atom]} event]
  (go (let [editor-1 (d/pull @conn '[*] (:db/id editor))
            frame-1 (d/pull @conn '[*] (get-in editor-1 [:editor/current-frame :db/id]))
            stored-data (:frame/db-string frame-1)
            stored-db (when stored-data (dt/read-transit-str stored-data))
            scene-system (system.solar/create-system! {:initial-db stored-db})]
        (swap! instance-atom assoc :scene-system scene-system)
        (p/transact! conn  [{:db/id (:db/id editor-1)
                             :editor/status :ready}]))))


(defn init-service! [props {:keys [service-chan] :as env}]
  (go-loop []
    (let [event (<! service-chan)]
      (try
        (cond
          (vector? event) (go-loop [[e & rs] event]
                            (when (seq e)
                              (let [_rst (<! (handle-event! props env e))]
                                (recur rs))))
          :else (handle-event! props env event))
        (catch js/Error e
          (println "editor service error: " e))))
    (recur)))




