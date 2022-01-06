(ns fancoil.async.subscriber
  (:require
   [integrant.core :as ig]
   [cljs.core.async :as async]))


(defmethod ig/init-key :fancoil/async.subscriber
  [_key config]
  (let [{:keys [publication preload-services]} config
        subscriber-atom (atom {})]
    (doseq [[listen-name service] preload-services]
      (let [service-chan (:in-chan service)]
        (swap! subscriber-atom assoc listen-name service-chan)
        (async/sub publication listen-name (:in-chan service))))
    {:publication publication
     :subscriber-atom subscriber-atom}))
