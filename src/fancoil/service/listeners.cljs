(ns fancoil.service.listeners
  (:require
   [integrant.core :as ig]))

;; 


(defmethod ig/init-key :fancoil/service.listeners
  [_key config]
  (let [{:keys [init-fn publication listeners props env]} config]
    (init-fn publication listeners props env)
    {:publication publication}))


