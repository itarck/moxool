(ns methodology.lib.client
  (:require
   [cljs.core.async :refer [go >! <! chan]]
   [cljs-http.client :as http]
   [datascript.transit :as dt]
   ))


(defn save-db-file [db-value file-path]
  (go
    (let [response (<! (http/post "/api/db/save" {:edn-params {:db-name file-path
                                                               :db-value (dt/write-transit-str db-value)}}))]
      (println (:body response)))))