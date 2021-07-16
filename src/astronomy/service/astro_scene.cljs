(ns astronomy.service.astro-scene
  (:require
   [posh.reagent :as p]
   [datascript.core :as d]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]
   [astronomy.model.astro-scene :as m.astro-scene]))


(defmulti handle-event! (fn [props env event] (:event/action event)))

(defmethod handle-event! :astro-scene/refresh
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [astro-scene @(p/pull conn '[*] (get-in props [:astro-scene :db/id]))
        tx (m.astro-scene/refresh-tx @conn astro-scene)]
    (p/transact! conn tx)))

(defmethod handle-event! :astro-scene/change-reference
  [props {:keys [conn service-chan]} {:event/keys [detail]}]
  (let [astro-scene-id (get-in props [:astro-scene :db/id])
        {:keys [reference-name]} detail]
    (p/transact! conn [[:db/add astro-scene-id :astro-scene/reference [:reference/name reference-name]]])
    (go (>! service-chan #:event{:action :astro-scene/refresh}))))


(defn init-service! [props {:keys [process-chan] :as env}]
    (println "astro-scene started")
    (go-loop []
      (let [event (<! process-chan)]
        (try
          (handle-event! props env event)
          (catch js/Error e
            (js/console.log e))))
      (recur)))