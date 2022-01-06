(ns fancoil.service.listener
  (:require
   [cljs.spec.alpha :as s]
   [cljs.core.async :refer [chan] :as async]
   [integrant.core :as ig]))

;; 

(defmethod ig/init-key :fancoil/service.listener
  [_key config]
  (let [{:keys [listen-name publication out-chan]} config]
    (async/sub publication listen-name out-chan)))

