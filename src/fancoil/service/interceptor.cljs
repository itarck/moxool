(ns fancoil.service.interceptor
  (:require
   [cljs.core.async :refer [go-loop >! <! go chan]]
   [integrant.core :as ig]
   [sieppari.core :as s]))


(defn start-service! [{:keys [chain-library in-chan out-chan]}]
  (go-loop []
    (let [event (<! in-chan)
          {:event/keys [action]} event
          chain (get @chain-library action)]
      (try
        (s/execute chain
                   {:event event}
                   (fn [response]
                     (when response
                       (cond
                         (vector? response) (go
                                              (doseq [event response]
                                                (>! out-chan event)))
                         (map? response) (go (>! out-chan response))
                         :else nil)))
                   println)
        (catch js/Error e
          (js/console.log e))))
    (recur)))


(defn create-core! [{:keys [in-chan out-chan]}]
  {:chain-library (atom {})
   :in-chan (or in-chan (chan))
   :out-chan out-chan})


(defmethod ig/init-key :fancoil/service.interceptor
  [_key config]
  (let [self (create-core! config)]
    (start-service! self)
    self))
