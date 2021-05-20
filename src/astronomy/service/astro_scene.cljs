(ns astronomy.service.astro-scene
  (:require
   [posh.reagent :as p]
   [datascript.core :as d]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]
   [astronomy.model.astro-scene :as m.astro-scene]))


(defmulti handle-event! (fn [props env event] (:event/action event)))



(defn init-service! [props {:keys [process-chan] :as env}]
    (println "astro-scene started")
    (go-loop []
      (let [event (<! process-chan)]
        (try
          (handle-event! props env event)
          (catch js/Error e
            (js/console.log e))))
      (recur)))