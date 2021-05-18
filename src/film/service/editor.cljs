(ns film.service.editor
  (:require
   [cljs.core.async :as async :refer [go >! <! chan alts! timeout put! take! go-loop]]
   [cljs-http.client :as http]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [film.model.player :as m.player]
   [film.model.video :as m.video]
   [film.model.editor :as m.editor]))


;; service 

(defn download-system-conn [value export-name]
  (let [data-blob (js/Blob. #js [(str value)] #js {:type "application/edn"})
        link (.createElement js/document "a")]
    (set! (.-href link) (.createObjectURL js/URL data-blob))
    (.setAttribute link "download" export-name)
    (.appendChild (.-body js/document) link)
    (.click link)
    (.removeChild (.-body js/document) link)))


(defn init-service! [props env]
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

            (println "service.system: event not match" signal))
            (catch js/Error e
              (do
                (js/console.log "!error in service editor: " e)
                (println props editor-id)))
          ))
      (recur))))
