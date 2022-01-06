(ns fancoil.db.conn
  (:require
   [datascript.core :as d]
   [integrant.core :as ig]))


;; conn 模块：datascript数据库
;; config  
;; #_{:schema schema
;;    :initial-tx initial-tx
;;    :initial-db db}
;; instance 


(defmethod ig/init-key :fancoil/db.conn [_k config]
  (let [{:keys [schema initial-tx initial-db]
         :or {schema {}}} config
        conn (d/create-conn schema)]
    (when initial-tx
      (d/transact! conn initial-tx))
    (when initial-db
      (d/reset-conn! conn initial-db))
    conn))


(defmethod ig/halt-key! :fancoil/db.conn [_k conn]
  (println "halt fancoil/conn"))
