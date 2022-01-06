(ns fancoil.service.processor
  (:require
   [cljs.core.async :refer [go-loop >! <!]]
   [integrant.core :as ig]))

;; go service 

(defmethod ig/init-key :fancoil/service.processor
  [_key config]
  (let [{:keys [env processor-fn]} config
        {:keys [in-chan out-chan]} env]
    (go-loop []
      (let [request (<! in-chan)]
        (try
          (let [response (processor-fn env request)]
            (when out-chan
              (>! out-chan response)))
          (catch js/Error e
            (js/console.log e))))
      (recur))))


