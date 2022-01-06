(ns fancoil.daemon
  (:require
   [cljs.spec.alpha :as s]
   [integrant.core :as ig]))

;; schema

(s/def ::config.init-fn fn?)
(s/def ::config.env map?)
(s/def ::config
  (s/keys :req [::config.init-fn
                ::config.env]))


;; daemon module: init a daemon instance
;; {:init-fn init-service!
;;  :env {:service-chan (ig/ref :scene/chan)
;;        :scene-conn (ig/ref :scene/conn)}}

(defmethod ig/init-key :fancoil/daemon
  [_key config]
  (let [{:keys [env init-fn]} config]
    (init-fn env)))
