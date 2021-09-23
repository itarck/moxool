(ns film2.modules.recorder.s
  (:require
   [cljs.core.async :as async :refer [go >! <! chan alts! timeout put! take! go-loop]]
   [cljs-http.client :as http]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]

   [film2.modules.ioframe.m :as ioframe.m]
   [film2.modules.recorder.m :as recorder.m]))


;; service 


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :recorder/change-menu
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [recorder menu-ident]} detail
        tx [{:db/id (:db/id recorder)
             :recorder/current-menu menu-ident}]]
    (p/transact! conn tx)))

(defmethod handle-event! :recorder/change-iovideo-temp-name
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [recorder temp-name]} detail
        tx [{:db/id (:db/id recorder)
             :recorder/iovideo-temp-name temp-name}]]
    (p/transact! conn tx)))

(defmethod handle-event! :recorder/create-iovideo
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [recorder iovideo-name]} detail
        tx (recorder.m/create-iovideo-tx recorder iovideo-name)]
    (p/transact! conn tx)))


(defmethod handle-event! :recorder/change-ioframe-copy-source
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [recorder ioframe]} detail
        tx [{:db/id (:db/id recorder)
             :recorder/ioframe-copy-source-id (:db/id ioframe)}]]
    (when-not (= :none (:db/id ioframe))
      (p/transact! conn tx))))


(defmethod handle-event! :recorder/copy-ioframe
  [props {:keys [conn service-chan]} {:event/keys [detail]}]
  (let [{:keys [recorder]} detail
        ioframe-source (d/pull @conn '[*] (get-in recorder [:recorder/ioframe-copy-source-id]))
        current-iovideo (d/pull @conn '[* {:iovideo/initial-ioframe [*]}] (get-in recorder [:recorder/current-iovideo :db/id]))
        old-initial-ioframe (:iovideo/initial-ioframe current-iovideo)
        ioframe-target (-> ioframe-source
                           (assoc :db/id (:db/id old-initial-ioframe))
                           (assoc :ioframe/name (:ioframe/name old-initial-ioframe)))
        tx [{:db/id (:db/id current-iovideo)
             :iovideo/initial-ioframe ioframe-target}]]
    (p/transact! conn tx)
    (go (>! service-chan #:event{:action :recorder/load-current-iovideo
                                 :detail {:recorder recorder}}))))


(defmethod handle-event! :recorder/load-current-iovideo
  [_props {:keys [conn instance-atom]} {:event/keys [detail]}]
  (let [recorder-1 (d/pull @conn '[*] (get-in detail [:recorder :db/id]))
        iovideo-1 (d/pull @conn '[*] (get-in recorder-1 [:recorder/current-iovideo :db/id]))
        ioframe-1 (d/pull @conn '[*] (get-in iovideo-1 [:iovideo/initial-ioframe :db/id]))
        ioframe-system (ioframe.m/create-ioframe-system ioframe-1)]
    (swap! instance-atom assoc-in [:iovideo (:db/id iovideo-1)] ioframe-system)
    (p/transact! conn [{:db/id (:db/id recorder-1)
                        :recorder/last-updated (js/Date.)}])))


#_(defn download-system-conn [value export-name]
    (let [data-blob (js/Blob. #js [(str value)] #js {:type "application/edn"})
          link (.createElement js/document "a")]
      (set! (.-href link) (.createObjectURL js/URL data-blob))
      (.setAttribute link "download" export-name)
      (.appendChild (.-body js/document) link)
      (.click link)
      (.removeChild (.-body js/document) link)))


#_(defn init-service! [props env]
    (let [{:keys [in-chan out-chan system-conn scene-system]} env
          scene-conn (:system/conn scene-system)
          editor-id (get-in props [:editor :db/id])]
      (go-loop []
        (let [[_service signal event] (<! in-chan)]
          (try
            (case signal
              :editor/start-record (do
                                     (m.editor/create-video! system-conn editor-id (dt/write-transit-str @scene-conn))
                                     (m.editor/start-record! system-conn scene-conn editor-id))

              :editor/stop-record (m.editor/stop-record! system-conn scene-conn editor-id)

              :editor/download-db (download-system-conn (dt/write-transit-str @system-conn) "store-system-conn.edn")

              :editor/save-db (go (let [{:keys [db-name]} event
                                        response (<! (http/post "/api/db/save" {:edn-params {:db-name db-name
                                                                                             :db-value (dt/write-transit-str @system-conn)}}))]
                                    (println (:body response))))
              :editor/load-db (go (let [{:keys [db-name]} event
                                        response (<! (http/post "/api/db/load" {:edn-params {:db-name db-name}}))
                                        db-value (:db-value (:body response))]
                                    (when db-value
                                      (d/reset-conn! system-conn (dt/read-transit-str db-value)))))
              :editor/upload-mp3 (let [{:keys [video filename file]} event]
                                   (p/transact! system-conn [{:db/id (:db/id video)
                                                              :video/mp3 (str "mp3/" filename)}])
                                   (http/post "/api/mp3/upload" {:multipart-params [[:filename filename] [:file file]]}))

              (println "service.system: event not match" signal))
            (catch js/Error e
              (do
                (js/console.log "!error in service editor: " e)
                (println props editor-id)))))
        (recur))))


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
          (println "recorder service error: " e))))
    (recur)))