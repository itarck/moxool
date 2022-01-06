(ns fancoil.core
  (:require
   [fancoil.value]
   [fancoil.daemon]
   [fancoil.db.atom]
   [fancoil.db.ratom]
   [fancoil.db.conn]
   [fancoil.db.pconn]
   [fancoil.async.chan]
   [fancoil.async.publisher]
   [fancoil.async.subscriber]
   [fancoil.reactor.base]
   [fancoil.view.reagent-view]
   [fancoil.view.rum-view]
   [fancoil.service.processor]
   [fancoil.service.interceptor]
   [fancoil.service.listener]
   [fancoil.service.listener-group]
   [fancoil.service.listeners]
   [fancoil.service.fx.pconn-transactor]
   [fancoil.service.fx.dconn-transactor]
   [fancoil.service.fx.logger]
   [fancoil.service.fx.http-client]
   [fancoil.service.fx.local-storage]))



(defn load-hierarchy! [hierarchy]
  (doseq [[tag parent] hierarchy]
    (derive tag parent)))


(defn merge-config [default-config user-config]
  (merge-with merge default-config user-config))

