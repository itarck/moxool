(ns astronomy.ig.service-center
  (:require
   [cljs.core.async :as async :refer [go >! <! chan go-loop]]
   [integrant.core :as ig]
   [astronomy.service.effect :as s.effect]

   [methodology.service.camera :as s.camera]
   [methodology.service.mouse :as s.mouse]
   [astronomy.service.keyboard-listener :as s.keyboard-listener]
   [astronomy.service.tool :as s.tool]
   [astronomy.service.user :as s.user]
   [astronomy.service.astro-scene :as s.astro-scene]
   [astronomy.service.universe-tool :as s.universe-tool]
   [astronomy.service.clock-tool :as s.clock-tool]
   [astronomy.service.info-tool :as s.info-tool]
   [astronomy.service.spaceship-camera-control :as s.spaceship]
   [astronomy.service.ppt-tool :as s.ppt-tool]
   [astronomy.service.contellation-tool :as s.constellation-tool]
   [astronomy.service.atmosphere-tool :as s.atmosphere-tool]
   [astronomy.service.horizon-coordinate-tool :as s.horizon-coordinate]
   [astronomy.service.terrestrial-coordinate-tool :as s.terrestrial-coordinate-tool]
   [astronomy.service.astronomical-point-tool :as s.astronomical-point-tool]
   [astronomy.service.ruler-tool :as s.ruler-tool]

   [astronomy.objects.planet.h :as planet.h]
   [astronomy.objects.satellite.h :as satellite.h]
   [astronomy.objects.ecliptic.h :as ecliptic.h]
   [astronomy.objects.astronomical-coordinate.h :as astronomical-coordinate.h]


   [astronomy.tools.astronomical-coordinate-tool.h :as astronomical-coordinate-tool.h]
   [astronomy.tools.planet-tool.h :as planet-tool.h]
   [astronomy.tools.satellite-tool.h :as satellite-tool.h]
;; 
   ))


(def processes
  [#:process{:name "keyboard"
             :listen []
             :service-fn s.keyboard-listener/init-service!}
   #:process{:name "user"
             :listen ["user"]
             :service-fn s.user/init-service!}
   #:process{:name "astro-scene"
             :listen ["astro-scene"]
             :service-fn s.astro-scene/init-service!}
   #:process{:name "planet"
             :listen ["planet" "clock.pub"]
             :handle-event-fn planet.h/handle-event}
   #:process{:name "satellite"
             :listen ["satellite"]
             :handle-event-fn satellite.h/handle-event}
   #:process{:name "astronomical-coordinate"
             :listen ["astronomical-coordinate"]
             :handle-event-fn astronomical-coordinate.h/handle-event}
   #:process{:name "tool"
             :listen ["tool"]
             :handle-event-fn s.tool/handle-event}
   #:process{:name "universe-tool"
             :listen ["universe-tool"]
             :service-fn s.universe-tool/init-service!}
   #:process{:name "clock-tool"
             :listen ["clock-tool"]
             :service-fn s.clock-tool/init-service!}
   #:process{:name "info-tool"
             :listen ["info-tool"]
             :service-fn s.info-tool/init-service!}
   #:process{:name "spaceship-camera-control"
             :listen ["spaceship-camera-control" "astro-scene.pub"]
             :handle-event-fn s.spaceship/handle-event}
   #:process{:name "ppt-tool"
             :listen ["ppt-tool"]
             :service-fn s.ppt-tool/init-service!}
   #:process{:name "constellation-tool"
             :listen ["constellation-tool"]
             :service-fn s.constellation-tool/init-service!}
   #:process{:name "atmosphere-tool"
             :listen ["atmosphere-tool"]
             :service-fn s.atmosphere-tool/init-service!}
   #:process{:name "horizon-coordinate"
             :listen ["horizon-coordinate"]
             :service-fn s.horizon-coordinate/init-service!}

   #:process{:name "astronomical-coordinate-tool"
             :listen ["astronomical-coordinate-tool"]
             :handle-event-fn astronomical-coordinate-tool.h/handle-event}
   #:process{:name "terrestrial-coordinate-tool"
             :listen ["terrestrial-coordinate-tool"]
             :service-fn s.terrestrial-coordinate-tool/init-service!}
   #:process{:name "astronomical-point-tool"
             :listen ["astronomical-point-tool"]
             :handle-event-fn s.astronomical-point-tool/handle-event}
   #:process{:name "ruler-tool"
             :listen ["ruler-tool"]
             :handle-event-fn s.ruler-tool/handle-event}
   #:process{:name "ecliptic"
             :listen ["ecliptic"]
             :handle-event-fn ecliptic.h/handle-event}
   #:process{:name "planet-tool"
             :listen ["planet-tool"]
             :handle-event-fn planet-tool.h/handle-event}
   #:process{:name "satellite-tool"
             :listen ["satellite-tool"]
             :handle-event-fn satellite-tool.h/handle-event}

   #:process{:name "camera"
             :listen []
             :service-fn s.camera/init-service!}
   #:process{:name "mouse"
             :listen ["mouse"]
             :service-fn s.mouse/init-service!}])


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


(defn init-service-center! [processes props env]
  ;; (println "!!!!! init service-center......")
  (let [{:keys [service-chan]} env
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


(def default-config
  #:service {:processes processes
             :props {:user {:db/id [:person/name "dr who"]}
                     :astro-scene {:db/id [:scene/name "solar"]}
                     :camera {:db/id [:camera/name "default"]}
                     :spaceship-camera-control {:db/id [:spaceship-camera-control/name "default"]}}
             :env {:service-chan (ig/ref ::chan)
                   :conn (ig/ref ::conn)
                   :state-atom (ig/ref ::state-atom)
                   :dom-atom (ig/ref ::dom-atom)}})


(defmethod ig/init-key :astronomy/service-center [_key config]
  (let [{:service/keys [processes props env]} (merge default-config config)
        service-chan (get-in env [:service-chan])]

    (init-service-center! processes props env)

    {:service-chan service-chan}))