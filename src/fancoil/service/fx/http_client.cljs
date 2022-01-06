(ns fancoil.service.fx.http-client
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [go <! >! go-loop chan] :as async]
            [cljs.core.async.impl.channels]
            [integrant.core :as ig]))


(defn init-http-client-service!
  [in-chan out-chan]
  (go-loop []
    (let [{:event/keys [action detail callback-chan callback-action]} (<! in-chan)
          {:keys [uri method data]} detail]
      (go (let [response (<!
                          (case method
                            "post" (http/post uri data)
                            "get" (http/get uri data)
                            (println "method not found")))]

            (when response
              (cond
                callback-chan (>! callback-chan response)
                callback-action (>! out-chan #:event{:action callback-action
                                                     :detail response})
                :else nil)))))
    (recur)))


(defmethod ig/init-key :fancoil/service.fx.http-client
  [_key config]
  (let [{:keys [in-chan out-chan]} config
        in-chan (or in-chan (chan))]
    (init-http-client-service! in-chan out-chan)
    {:in-chan in-chan
     :out-chan out-chan}))


#_(comment

  (go (let [response (<! (http/post "/todomvc/dummy-post"
                                    {:transit-params {:key1 "value1" :key2 "value2"}
                                     :headers {"Accept" "application/transit+json"}}))]
        (prn response)
        (prn (:body response))))
  
  (def sample
    {:uri "/todomvc/dummy-post"
     :method "post"
     :data {:transit-params {:key1 "value1" :key2 "value2"}
            :headers {"Accept" "application/transit+json"}}})

  )