(ns film2.modules.editor.s
  (:require
   [cljs.core.async :refer [go go-loop >! <!]]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [integrant.core :as ig]
   [posh.reagent :as p]
   [cljs-http.client :as http]
   [methodology.lib.circuit]
   [film2.modules.ioframe.m :as ioframe.m]))


(defmulti handle-event! (fn [_ _ event] (:event/action event)))


(defmethod handle-event! :editor/change-current-ioframe
  [{:keys [editor]} {:keys [conn service-chan]} {:event/keys [detail]}]
  (let [{:keys [editor ioframe]} detail
        tx [{:db/id (:db/id editor)
             :editor/current-ioframe (:db/id ioframe)}]]
    (p/transact! conn tx)))


(defmethod handle-event! :editor/load-current-ioframe
  [_props {:keys [conn instance-atom] :as env} {:event/keys [detail]}]
  (go (let [editor-1 (d/pull @conn '[*] (get-in detail [:editor :db/id]))
            ioframe-1 (d/pull @conn '[*] (get-in editor-1 [:editor/current-ioframe :db/id]))
            scene-system (ioframe.m/create-ioframe-system ioframe-1)
            old-scene-system (get-in @instance-atom [:ioframe (:db/id ioframe-1)])]
        (when old-scene-system
          (ig/halt! (:ioframe-system/ig-instance old-scene-system)))
        (swap! instance-atom assoc-in [:ioframe (:db/id ioframe-1)] scene-system)
        (p/transact! conn  [{:db/id (:db/id editor-1)
                             :editor/status :ready
                             :editor/last-updated (js/Date.)}]))))


(defn init-service! [props {:keys [process-chan] :as env}]
  (go-loop []
    (let [event (<! process-chan)]
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

