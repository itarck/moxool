(ns astronomy.service.astro-scene
  (:require
   [posh.reagent :as p]
   [datascript.core :as d]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]
   [astronomy.model.astro-scene :as m.astro-scene]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :astro-scene/clock-changed
  [{:keys [astro-scene]} {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [clock-id]} detail
        astro-scene-id (:db/id astro-scene)
        tx (m.astro-scene/update-by-clock-tx @conn astro-scene-id clock-id)]
    (p/transact! conn tx)))


(defn init-service! [props {:keys [process-chan] :as env}]
    (println "astro-scene started")
    (go-loop []
      (let [event (<! process-chan)]
        (try
          (handle-event! props env event)
          (catch js/Error e
            (js/console.log e))))
      (recur)))