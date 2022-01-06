(ns fancoil.service.listener-group
  (:require
   [cljs.spec.alpha :as s]
   [cljs.core.async :refer [chan] :as async]
   [integrant.core :as ig]))



(defmethod ig/init-key :fancoil/service.listener-group
  [_key config]
  (let [{:keys [listeners env]} config
        {:keys [publication]} env]
    (doseq [listener listeners]
      (let [{:keys [listen-name init-fn]} listener
            in-chan (chan)
            listener-env (assoc env :in-chan in-chan)]
        (async/sub publication listen-name in-chan)
        (init-fn listener-env)))))


(comment 
  (def sample
    #:service.listener-group
     {:listens [{:listen-name :tx :init-fn (fn [])}]
      :env {:conn (ig/ref :todomvc2/conn)
            :publication (ig/ref :todomvc2/effect-publisher)
            :error-chan (ig/ref :todomvc2/effect-chan)}})
  ;
  )