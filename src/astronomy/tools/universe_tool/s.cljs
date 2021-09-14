(ns astronomy.tools.universe-tool.s
  (:require
   [applied-science.js-interop :as j]
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :universe-tool/load-object
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [object-id show?]} detail]
    (p/transact! conn [{:db/id object-id
                        :object/show? show?}])))


(defmethod handle-event! :universe-tool/change-celestial-scale
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astro-scene-id celestial-scale]} detail]
    (p/transact! conn [{:db/id astro-scene-id
                        :astro-scene/celestial-scale celestial-scale}])))


(defn init-service! [props {:keys [process-chan] :as env}]
  (println "universe tool started")
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (cond
            (re-find #"No method in multimethod" (j/get e :message)) nil
            :else (println "universe tool error: " e)))))
    (recur)))
