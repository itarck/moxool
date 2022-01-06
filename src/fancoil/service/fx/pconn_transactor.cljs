(ns fancoil.service.fx.pconn-transactor
  (:require
   [cljs.spec.alpha :as s]
   [cljs.core.async :refer [go-loop <! >! chan] :as async]
   [integrant.core :as ig]
   [posh.reagent :as p]))


(defn init-pconn-transactor-service!
  [in-chan conn]
  (go-loop []
    (let [{:event/keys [action detail]} (<! in-chan)]
      (try
        (case action
          :tx/tx (p/transact! conn detail)
          nil)
        (catch js/Error e
          (println [:error (str e)]))))
    (recur)))


(defmethod ig/init-key :fancoil/service.fx.pconn-transactor
  [_key config]
  (let [{:keys [conn]} config
        in-chan (chan)]
    (init-pconn-transactor-service! in-chan conn)
    {:in-chan in-chan
     :conn conn}))


(comment
  (def sample
    #:service.listener.pconn-transactor
     {:listen-name :tx
      :env {
            :conn (ig/ref :todomvc2/conn)
            :publication (ig/ref :todomvc2/effect-publisher)
            :error-chan (ig/ref :todomvc2/effect-chan)}})
  
  (s/explain :service.listener.pconn-transactor/config 
            sample)
  ;; 
  )