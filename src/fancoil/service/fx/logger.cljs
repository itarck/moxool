(ns fancoil.service.fx.logger
  (:require
   [cljs.core.async :refer [go-loop <! >! chan] :as async]
   [integrant.core :as ig]))


(defn logger-handler
  [{[action log] :event}]
  (println (str action ": " log)))


(defn init-logger-service!
  [in-chan]
  (go-loop []
    (let [event (<! in-chan)]
      (println "logging: " (str event)))
    (recur)))


(defmethod ig/init-key :fancoil/service.fx.logger
  [_key _config]
  (let [in-chan (chan)]
    (init-logger-service! in-chan)
    {:in-chan in-chan}))


