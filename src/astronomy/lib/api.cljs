(ns astronomy.lib.api
  (:require
   [cljs.core.async :refer [go >! <! chan]]
   [cljs-http.client :as http]
   [datascript.transit :as dt]))

(def host
  "http://localhost:7000")


(defn save-db-file [db-value file-path]
  (go
    (let [response (<! (http/post (str host "/api/db/save")
                                  {:edn-params {:db-name file-path
                                                :db-value (dt/write-transit-str db-value)}
                                   :with-credentials? false}))]
      (println (:body response)))))




(comment

  (def host
    "http://localhost:7000")

  (go
    (let [response (<! (http/get (str host "/api/dummy-get")
                                 {:with-credentials? false}))]
      (println (:body response))))
  
  ;; 
  )