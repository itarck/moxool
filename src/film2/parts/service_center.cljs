(ns film2.parts.service-center
  (:require
   [cljs.core.async :as async :refer [go >! <! chan go-loop]]
   [integrant.core :as ig]
   [astronomy.service.effect :as s.effect]
   [film2.modules.studio.h :as studio.h]
   [film2.modules.editor.s :as editor.s]
   [film2.modules.player.s :as player.s]
   [film2.modules.recorder.s :as recorder.s]
   [film2.modules.cinema.s :as cinema.s]
   
;; 
   ))


(def process-lib
  {:studio #:process {:name "studio"
                      :listen ["studio"]
                      :handle-event-fn studio.h/handle-event}
   :editor #:process{:name "editor"
                     :listen ["editor"]
                     :service-fn editor.s/init-service!}
   :player #:process {:name "player"
                      :listen ["player"]
                      :service-fn player.s/init-service!}
   :recorder #:process {:name "recorder"
                        :listen ["recorder"]
                        :service-fn recorder.s/init-service!}
   :cinema #:process {:name "cinema"
                      :listen ["cinema"]
                      :handle-event-fn cinema.s/handle-event}})


(defn init-service! [props env]
  (let [{:keys [process-name process-chan handle-event-fn conn]} env]
    (go-loop []
      (let [event (<! process-chan)
            handler-env {:db @conn}]
        (try
          (let [effect (handle-event-fn props handler-env event)]
            (if (or (seq? effect) (vector? effect))
              (doseq [eft effect]
                (s.effect/handle-effect! eft env))
              (s.effect/handle-effect! effect env)))
          (catch js/Error e
            (println process-name ": no handler found for event" (:event/action event)))))
      (recur))))


(defn handle-event-process->service
  [props {:keys [process-name process-chan handle-event-process] :as env}]
  
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (cond
          (vector? event) (go-loop [[e & rs] event]
                            (when (seq e)
                              (let [_rst (<! (handle-event-process props env e))]
                                (recur rs))))
          :else (handle-event-process props env event))
        (catch js/Error e
          (println process-name "service error: " e))))
    (recur)))


(defn init-service-center! [processes props env]
  ;; (println "!!!!! init service-center......")
  (let [{:keys [service-chan]} env
        process-dispatch-fn (fn [event]
                              (namespace (:event/action event)))
        process-publication (async/pub service-chan process-dispatch-fn)]

    (doseq [{:process/keys [service-fn handle-event-fn listen] process-name :process/name} processes]
      (let [process-chan (chan)]
        (println (str "process: " process-name " started....."))
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



(defmethod ig/init-key :studio/service-center [_key config]
  (let [{:keys [processes props env]} config
        process-kvs (if processes
                      (select-keys process-lib processes)
                      process-lib)
        service-chan (get-in env [:service-chan])]

    (println "init service center start: " (js/Date))
    (init-service-center! (vals process-kvs) props env)
    (println "init service center end: " (js/Date))
    {:service-chan service-chan}))


(derive :cinema/service-center :studio/service-center)

