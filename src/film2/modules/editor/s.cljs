(ns film2.modules.editor.s
  (:require
   [cljs.core.async :refer [go go-loop >! <!]]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [cljs-http.client :as http]
   [methodology.lib.circuit]
   [film2.modules.ioframe.m :as ioframe.m]))


(defmulti handle-event! (fn [_ _ event] (:event/action event)))

#_(defmethod handle-event! :editor/pull-current-frame
  [{:keys [editor]} {:keys [conn]} event]
  (let [editor-1 (d/pull @conn '[*] (:db/id editor))
        frame-1 (d/pull @conn '[*] (get-in editor-1 [:editor/current-frame :db/id]))
        db-url (get-in frame-1 [:frame/name])]
    (go (let [response (<! (http/get db-url))
              stored-data (:body response)]
          (p/transact! conn [{:db/id (:db/id frame-1)
                              :frame/db-string stored-data
                              :frame/db (dt/read-transit-str stored-data)}])))))


(defmethod handle-event! :editor/change-current-ioframe
  [{:keys [editor]} {:keys [conn service-chan]} {:event/keys [detail]}]
  (let [{:keys [editor ioframe]} detail
        tx [{:db/id (:db/id editor)
             :editor/current-frame (:db/id ioframe)}]]
    (p/transact! conn tx)
    #_(go (>! service-chan #:event{:action :editor/load-current-frame}))))


(defmethod handle-event! :editor/load-current-frame
  [{:keys [editor]} {:keys [conn instance-atom]} event]
  (go (let [editor-1 (d/pull @conn '[*] (:db/id editor))
            ioframe-1 (d/pull @conn '[*] (get-in editor-1 [:editor/current-frame :db/id]))
            scene-system (ioframe.m/create-ioframe-system ioframe-1)]
        (swap! instance-atom assoc-in [:ioframe (:db/id ioframe-1)] scene-system)
        (p/transact! conn  [{:db/id (:db/id editor-1)
                             :editor/status :ready
                             :editor/last-updated (js/Date.)}]))))


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

