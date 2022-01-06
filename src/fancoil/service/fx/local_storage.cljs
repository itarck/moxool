(ns fancoil.service.fx.local-storage
  (:require
   [cljs.core.async :refer [go <! >! go-loop chan] :as async]
   [integrant.core :as ig]))


(defn set-key! [key item]
  (js/localStorage.setItem (str key) (str item))
  nil)

(defn get-key [key]
  (js/localStorage.getItem (str key)))


(defn init-local-storage-service!
  [{:keys [in-chan out-chan] :as self}]
  (go-loop []
    (let [request (<! in-chan)
          {:event/keys [action detail callback]} request]
      (go (let [response (case action
                           :local-storage/set (let [{:keys [key item]} detail]
                                                (set-key! key item))
                           :local-storage/get (let [{:keys [key]} detail]
                                                (get-key key)))]
            (when response
              (if callback
                (>! callback response)
                (>! out-chan response))))))
    (recur)))


(defmethod ig/init-key :fancoil/service.fx.local-storage
  [_key config]
  (let [{:keys [in-chan out-chan]} config
        in-chan (or in-chan (chan))
        self {:in-chan in-chan
              :out-chan out-chan}]
    (init-local-storage-service! self)
    self))


