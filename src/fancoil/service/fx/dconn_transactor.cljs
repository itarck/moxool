(ns fancoil.service.fx.dconn-transactor
  (:require
   [cljs.spec.alpha :as s]
   [cljs.core.async :refer [go-loop <! >! chan] :as async]
   [integrant.core :as ig]
   [datascript.core :as d]))


(defn init-dconn-transactor-service!
  [in-chan conn]
  (go-loop []
    (let [{:event/keys [action detail]} (<! in-chan)]
      (try
        (case action
          :tx/tx (d/transact! conn detail)
          nil)
        (catch js/Error e
          (println [:error (str e)]))))
    (recur)))


(defmethod ig/init-key :fancoil/service.fx.dconn-transactor
  [_key config]
  (let [{:keys [conn]} config
        in-chan (chan)]
    (init-dconn-transactor-service! in-chan conn)
    {:in-chan in-chan
     :conn conn}))



(comment
  (def sample
    #:service.listener.dconn-transactor
     {:listen-name :tx
      :env {:conn (ig/ref :todomvc2/conn)
            :publication (ig/ref :todomvc2/effect-publisher)
            :error-chan (ig/ref :todomvc2/effect-chan)}})

  (s/explain :service.listener.dconn-transactor/config
             sample)
  ;; 
  )