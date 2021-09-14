(ns astronomy.service.core
  (:require
   [cljs.core.async :as async :refer [go >! <! chan go-loop]]
   [astronomy.service.effect :as s.effect]))


(defn init-service! [props env]
  (let [{:keys [process-name process-chan handle-event-fn conn dom-atom]} env]
    (go-loop []
      (let [event (<! process-chan)
            handler-env {:db @conn
                         :dom @dom-atom}]
        (try
          (let [effect (handle-event-fn props handler-env event)]
            (if (or (seq? effect) (vector? effect))
              (doseq [eft effect]
                (s.effect/handle-effect! eft env))
              (s.effect/handle-effect! effect env)))
          (catch js/Error e
            (println process-name ": no handler found for event" (:event/action event)))))
      (recur))))


(defn init-service-center! [props env]
  ;; (println "!!!!! init service-center......")
  (let [{:keys [service-chan processes]} env
        process-dispatch-fn (fn [event]
                              (namespace (:event/action event)))
        process-publication (async/pub service-chan process-dispatch-fn)]

    (doseq [{:process/keys [service-fn handle-event-fn listen] process-name :process/name} processes]
      (let [process-chan (chan)]
        (doseq [l listen]
          (async/sub process-publication (name l) process-chan))
        (when service-fn
          (service-fn props (-> env
                                (assoc :process-chan process-chan)
                                (assoc :process-name process-name))))
        (when handle-event-fn
          (init-service! props (-> env
                                   (assoc :process-chan process-chan)
                                   (assoc :process-name process-name)
                                   (assoc :handle-event-fn handle-event-fn))))))

    {:service-chan service-chan}))

