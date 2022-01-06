(ns fancoil.async.chan
  (:require
   [integrant.core :as ig]
   [cljs.core.async :refer [chan close!]]))


;; chan 模块: 不可配置，就是常用的chan
;; config: {}
;; instance： #object[cljs.core.async.impl.channels.ManyToManyChannel]


(defmethod ig/init-key :fancoil/async.chan 
  [_k _config]
  (chan))

(defmethod ig/halt-key! :fancoil/async.chan 
  [_k ch]
  (println "halt fancoil.async.chan")
  (close! ch))
