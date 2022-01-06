(ns fancoil.reactor.base
  (:require
   [integrant.core :as ig]))


(defn listen 
  [library-atom env request]
  (let [sub-fn (get @library-atom (first request))]
    (sub-fn env request)))


(defmethod ig/init-key :fancoil/reactor.base
  [_key config]
  (let [{:keys [library env]} config
        library-atom (atom (or library {}))]
    {:library-atom library-atom
     :listen-fn (partial listen library-atom env)}))
