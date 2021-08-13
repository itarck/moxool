(ns astronomy.service.core
  (:require
   [cljs.core.async :as async :refer [go >! <! chan go-loop]]
   [methodology.service.camera :as s.camera]
   [methodology.service.mouse :as s.mouse]
   [astronomy.service.keyboard-listener :as s.keyboard-listener]
   [astronomy.service.tool :as s.tool]
   [astronomy.service.effect :as s.effect]
   [astronomy.service.user :as s.user]
   [astronomy.service.astro-scene :as s.astro-scene]
   [astronomy.service.universe-tool :as s.universe-tool]
   [astronomy.service.clock-tool :as s.clock-tool]
   [astronomy.service.info-tool :as s.info-tool]
   [astronomy.service.spaceship-camera-control :as s.spaceship]
   [astronomy.service.ppt-tool :as s.ppt-tool]
   [astronomy.service.goto-celestial-tool :as s.goto-tool]
   [astronomy.service.contellation-tool :as s.constellation-tool]
   [astronomy.service.atmosphere-tool :as s.atmosphere-tool]
   [astronomy.service.horizon-coordinate-tool :as s.horizon-coordinate]
   [astronomy.service.astronomical-coordinate-tool :as s.astronomical-coordinate-tool]
   [astronomy.service.terrestrial-coordinate-tool :as s.terrestrial-coordinate-tool]
   [astronomy.service.astronomical-point-tool :as s.astronomical-point-tool]
   [astronomy.service.ruler-tool :as s.ruler-tool]
   
   [astronomy.omodule.ecliptic.handler :as h.ecliptic]))


(def processes
  [{:listen []
    :process-name "keyboard"
    :service-fn s.keyboard-listener/init-service!}
   {:listen [:user]
    :process-name "user"
    :service-fn s.user/init-service!}
   {:listen [:astro-scene]
    :process-name "astro-scene"
    :service-fn s.astro-scene/init-service!}

   {:listen [:tool]
    :process-name "tool"
    :handle-event-fn s.tool/handle-event}

   {:listen [:universe-tool]
    :process-name "universe-tool"
    :service-fn s.universe-tool/init-service!}
   {:listen [:clock-tool]
    :process-name "clock-tool"
    :service-fn s.clock-tool/init-service!}
   {:listen [:info-tool]
    :process-name "info-tool"
    :service-fn s.info-tool/init-service!}

   {:listen [:spaceship-camera-control :astro-scene.pub]
    :process-name "spaceship-camera-control"
    :handle-event-fn s.spaceship/handle-event}

   {:listen [:ppt-tool]
    :process-name "ppt-tool"
    :service-fn s.ppt-tool/init-service!}
   {:listen [:goto-celestial-tool]
    :process-name "goto-celestial-tool"
    :service-fn s.goto-tool/init-service!}
   {:listen [:constellation-tool]
    :process-name "constellation-tool"
    :service-fn s.constellation-tool/init-service!}
   {:listen [:atmosphere-tool]
    :process-name "atmosphere-tool"
    :service-fn s.atmosphere-tool/init-service!}
   {:listen [:horizon-coordinate]
    :process-name "horizon-coordinate"
    :service-fn s.horizon-coordinate/init-service!}
   {:listen [:astronomical-coordinate-tool]
    :process-name "astronomical-coordinate-tool"
    :handle-event-fn s.astronomical-coordinate-tool/handle-event}
   {:listen [:terrestrial-coordinate-tool]
    :process-name "terrestrial-coordinate-tool"
    :service-fn s.terrestrial-coordinate-tool/init-service!}
   {:listen [:astronomical-point-tool]
    :process-name "astronomical-point-tool"
    :handle-event-fn s.astronomical-point-tool/handle-event}
   {:listen [:ruler-tool]
    :process-name "ruler-tool"
    :handle-event-fn s.ruler-tool/handle-event}
   {:listen [:ecliptic]
    :process-name "ecliptic"
    :handle-event-fn h.ecliptic/handle-event}

   {:listen []
    :process-name "camera"
    :service-fn s.camera/init-service!}
   {:listen [:mouse]
    :process-name "mouse"
    :service-fn s.mouse/init-service!}])


(defn init-service! [props {:keys [process-name process-chan handle-event-fn conn dom-atom] :as env}]
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
    (recur)))


(defn init-service-center! [props env]
  ;; (println "!!!!! init service-center......")
  (let [{:keys [service-chan]} env
        process-dispatch-fn (fn [event]
                              (keyword (namespace (:event/action event))))
        process-publication (async/pub service-chan process-dispatch-fn)]

    (doseq [{:keys [service-fn handle-event-fn process-name listen]} processes]
      (let [process-chan (chan)]
        (doseq [l listen]
          (async/sub process-publication l process-chan))
        (when service-fn
          (service-fn props (-> env
                                (assoc :process-chan process-chan)
                                (assoc :process-name process-name))))
        (when handle-event-fn 
          (init-service! props (-> env
                                   (assoc :process-chan process-chan)
                                   (assoc :process-name process-name)
                                   (assoc :handle-event-fn handle-event-fn)))
          )
        ))

    ;; (kick-start! env)

    {:service-chan service-chan}))

