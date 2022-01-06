(ns fancoil.db.pconn
  (:require
   [datascript.core :as d]
   [datascript.transit :as dt]
   [integrant.core :as ig]
   [posh.reagent :as p]
))


;; conn 模块：datascript数据库，并加了 reagent posh
;; config  
;; #_{:schema schema
;;    :initial-tx initial-tx
;;    :initial-db db}
;; instance 


(defn create-conn!
  ([]
   (let [conn (d/create-conn)]
     (p/posh! conn)
     conn))
  ([schema]
   (let [conn (d/create-conn schema)]
     (p/posh! conn)
     conn)))


(defmethod ig/init-key :fancoil/db.pconn [_k config]
  (let [{:keys [schema initial-tx initial-db db-transit-str]} config
        conn (create-conn! schema)]
    (when initial-tx
      (d/transact! conn initial-tx))
    (when initial-db
      (d/reset-conn! conn initial-db))
    (when db-transit-str
      (d/reset-conn! conn (dt/read-transit-str db-transit-str)))
    #_(when db-url
        (go (let [response (<! (http/get db-url))
                  stored-data (:body response)
                  stored-db (when stored-data (dt/read-transit-str stored-data))]
              (d/reset-conn! conn stored-db))))
    (p/posh! conn)
    conn))

(defmethod ig/halt-key! :fancoil/db.pconn [_k conn]
  (println "halt fancoil.db.pconn"))
