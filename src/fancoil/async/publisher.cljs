(ns fancoil.async.publisher
  (:require
   [integrant.core :as ig]
   [cljs.core.async :as async]))


(defmethod ig/init-key :fancoil/async.publisher
  [_key config]
  (let [{:keys [in-chan pub-fn]} config
        publication (async/pub in-chan pub-fn)]
    publication))
