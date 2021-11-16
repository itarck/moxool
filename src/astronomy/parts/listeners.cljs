(ns astronomy.parts.listeners
  (:require
   [cljs.core.async :as async :refer [go >! <! chan go-loop]]
   [astronomy.service.effect :as s.effect]

   [astronomy.space.user.s :as s.user]
   [astronomy.space.camera.s :as s.camera]
   [astronomy.space.mouse.s :as s.mouse]
   [astronomy.space.keyboard.s :as s.keyboard-listener]
   [astronomy.space.tool.h :as s.tool]
   [astronomy.space.selector.h :as selector.h]

   [astronomy.objects.astro-scene.s :as s.astro-scene]
   [astronomy.objects.planet.h :as planet.h]
   [astronomy.objects.satellite.h :as satellite.h]
   [astronomy.objects.ecliptic.h :as ecliptic.h]
   [astronomy.objects.astronomical-coordinate.h :as astronomical-coordinate.h]

   [astronomy.tools.universe-tool.s :as s.universe-tool]
   [astronomy.tools.info-tool.s :as s.info-tool]
   [astronomy.tools.constellation-tool.s :as s.constellation-tool]
   [astronomy.tools.atmosphere-tool.s :as s.atmosphere-tool]
   [astronomy.tools.horizon-coordinate-tool.s :as s.horizon-coordinate]
   [astronomy.tools.terrestrial-coordinate-tool.s :as s.terrestrial-coordinate-tool]
   [astronomy.tools.ruler-tool.h :as s.ruler-tool]
   [astronomy.tools.ppt-tool.h :as s.ppt-tool]
   [astronomy.tools.clock-tool.s :as s.clock-tool]
   [astronomy.tools.astronomical-coordinate-tool.h :as astronomical-coordinate-tool.h]
   [astronomy.tools.astronomical-point-tool.h :as s.astronomical-point-tool]
   [astronomy.tools.spaceship-camera-control.s :as s.spaceship]
   [astronomy.tools.planet-tool.h :as planet-tool.h]
   [astronomy.tools.satellite-tool.h :as satellite-tool.h]
   [astronomy.tools.ellipse-orbit-tool.h :as ellipse-orbit-tool.h]
;; 
   ))



(def listeners
  [#:listener{:name "user"
              :listen ["user"]
              :service-fn s.user/init-service!}
   #:listener{:name "astro-scene"
              :listen ["astro-scene"]
              :service-fn s.astro-scene/init-service!}
   #:listener{:name "keyboard"
              :listen []
              :service-fn s.keyboard-listener/init-service!}
   #:listener{:name "camera"
              :listen []
              :service-fn s.camera/init-service!}
   #:listener{:name "mouse"
              :listen ["mouse"]
              :service-fn s.mouse/init-service!}
   #:listener {:name "selector"
               :listen ["selector"]
               :handle-event-fn selector.h/handle-event}

   #:listener{:name "planet"
              :listen ["planet" "clock.pub"]
              :handle-event-fn planet.h/handle-event}
   #:listener{:name "satellite"
              :listen ["satellite"]
              :handle-event-fn satellite.h/handle-event}
   #:listener{:name "astronomical-coordinate"
              :listen ["astronomical-coordinate"]
              :handle-event-fn astronomical-coordinate.h/handle-event}

   #:listener{:name "tool"
              :listen ["tool"]
              :handle-event-fn s.tool/handle-event}
   #:listener{:name "universe-tool"
              :listen ["universe-tool"]
              :service-fn s.universe-tool/init-service!}
   #:listener{:name "clock-tool"
              :listen ["clock-tool"]
              :service-fn s.clock-tool/init-service!}
   #:listener{:name "info-tool"
              :listen ["info-tool"]
              :service-fn s.info-tool/init-service!}
   #:listener{:name "spaceship-camera-control"
              :listen ["spaceship-camera-control" "astro-scene.pub"]
              :handle-event-fn s.spaceship/handle-event}
   #:listener{:name "ppt-tool"
              :listen ["ppt-tool"]
              :service-fn s.ppt-tool/init-service!}
   #:listener{:name "constellation-tool"
              :listen ["constellation-tool"]
              :service-fn s.constellation-tool/init-service!}
   #:listener{:name "atmosphere-tool"
              :listen ["atmosphere-tool"]
              :service-fn s.atmosphere-tool/init-service!}
   #:listener{:name "horizon-coordinate"
              :listen ["horizon-coordinate"]
              :service-fn s.horizon-coordinate/init-service!}

   #:listener{:name "astronomical-coordinate-tool"
              :listen ["astronomical-coordinate-tool"]
              :handle-event-fn astronomical-coordinate-tool.h/handle-event}
   #:listener{:name "terrestrial-coordinate-tool"
              :listen ["terrestrial-coordinate-tool"]
              :service-fn s.terrestrial-coordinate-tool/init-service!}
   #:listener{:name "astronomical-point-tool"
              :listen ["astronomical-point-tool"]
              :handle-event-fn s.astronomical-point-tool/handle-event}
   #:listener{:name "ruler-tool"
              :listen ["ruler-tool"]
              :handle-event-fn s.ruler-tool/handle-event}
   #:listener{:name "ecliptic"
              :listen ["ecliptic"]
              :handle-event-fn ecliptic.h/handle-event}
   #:listener{:name "planet-tool"
              :listen ["planet-tool"]
              :handle-event-fn planet-tool.h/handle-event}
   #:listener{:name "satellite-tool"
              :listen ["satellite-tool"]
              :handle-event-fn satellite-tool.h/handle-event}
   #:listener{:name "ellipse-orbit-tool"
              :listen ["ellipse-orbit-tool"]
              :handle-event-fn ellipse-orbit-tool.h/handle-event}])


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


(defn init-service-center! [publication listeners props env]
  ;; (println "!!!!! init service-center......")
  (doseq [{:listener/keys [service-fn handle-event-fn listen] process-name :listener/name} listeners]
    (let [process-chan (chan)]
      (doseq [l listen]
        (async/sub publication (name l) process-chan))
      (when service-fn
        (service-fn props (-> env
                              (assoc :process-chan process-chan)
                              (assoc :process-name process-name))))
      (when handle-event-fn
        (init-service! props (-> env
                                 (assoc :process-chan process-chan)
                                 (assoc :process-name process-name)
                                 (assoc :handle-event-fn handle-event-fn)))))))

