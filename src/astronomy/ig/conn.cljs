(ns astronomy.ig.conn
  (:require
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [cljs.core.async :refer [go >! <!]]
   [cljs-http.client :as http]
   [integrant.core :as ig]
   [astronomy.conn.schema :refer [schema]]))



(defmethod ig/init-key :astronomy/conn [_k config]
  (println "astronomy/conn start: " (js/Date))
  (let [{:conn/keys [db-url initial-db]} config
        conn (d/create-conn schema)]
    (when initial-db
      (d/reset-conn! conn initial-db))
    (when db-url
      (go (let [response (<! (http/get db-url))
                stored-data (:body response)
                stored-db (when stored-data (dt/read-transit-str stored-data))]
            (d/reset-conn! conn stored-db))))
    (p/posh! conn)
    (println "astronomy/conn end: " (js/Date))
    conn))