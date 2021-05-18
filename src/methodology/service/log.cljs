(ns methodology.service.log
  (:require
   [cljs.core.async :as async :refer [go >! <! go-loop]]))


(defn init-service! [props {:keys [service-chan] :as env}]
  (go-loop []
    (let [e (<! service-chan)]
      (println "logging: " e)
      (recur))))