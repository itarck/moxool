(ns astronomy.service.astro-scene
  (:require
   [posh.reagent :as p]
   [datascript.core :as d]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]
   [astronomy.model.astro-scene :as m.astro-scene]))


(defmulti handle-event! (fn [event _env] (:event/action event)))


(defmethod handle-event! :astro-scene/clock-changed
  [{:event/keys [detail]} {:keys [conn]}]
  (let [{:keys [clock-id]} detail
        db2 @conn
        tx2 (m.astro-scene/update-celestials-by-clock-tx db2 clock-id)
        db3 (d/db-with db2 tx2)
        tx3 (m.astro-scene/update-reference-tx db3)]
    (p/transact! conn (concat tx2 tx3))))


(defn init-service! [props {:keys [process-chan service-chan conn] :as env}]
  (let [{:keys [user]} props]
    (println "astro-scene started")
    (go-loop []
      (let [event (<! process-chan)
            process-props (merge props event)]
        (try
          (handle-event! process-props env)
          (catch js/Error e
            (js/console.log e))))
      (recur))))