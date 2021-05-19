(ns astronomy.service.astro-scene
  (:require
   [posh.reagent :as p]
   [datascript.core :as d]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]
   [astronomy.model.astro-scene :as m.astro-scene]))


(defmulti handle-event! (fn [event _env] (:event/action event)))


(defmethod handle-event! :astro-scene/clock-changed
  [{:event/keys [detail] :as event} {:keys [conn]}]
  (let [{:keys [clock-id]} detail
        astro-scene-id (:astro-scene-id event)
        tx (m.astro-scene/update-by-clock-tx @conn astro-scene-id clock-id)]
    (p/transact! conn tx)))


(defn init-service! [props {:keys [process-chan service-chan conn] :as env}]
  (let [{:keys [astro-scene]} props
        pprops {:astro-scene-id (:db/id astro-scene)}]
    (println "astro-scene started")
    (go-loop []
      (let [event (<! process-chan)
            process-props (merge pprops event)]
        (try
          (handle-event! process-props env)
          (catch js/Error e
            (js/console.log e))))
      (recur))))