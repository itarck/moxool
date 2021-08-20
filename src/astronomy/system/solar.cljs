(ns astronomy.system.solar
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [astronomy.service.meta :refer [init-meta-service!]]
   [astronomy.service.core :refer [init-service-center!]]
   [astronomy.view.core :refer [RootView]]
   [astronomy.model.core :refer [basic-db]]

  ;;  views
   [astronomy.view.star :as v.star]
   [astronomy.view.galaxy :as v.galaxy]
   [astronomy.view.constellation :as v.constel]
   [astronomy.view.terrestrial-coordinate :as v.terrestrial-coordinate]
   [astronomy.view.horizon-coordinate :as v.horizon-coordinate]

   [astronomy.view.user.universe-tool :as v.universe-tool]
   [astronomy.view.user.clock-tool :as v.clock-tool]
   [astronomy.view.user.spaceship-camera-control :as v.spaceship]
   [astronomy.view.user.info-tool :as v.info-tool]
   [astronomy.view.user.ppt-tool :as v.ppt-tool]
   [astronomy.view.user.goto-celestial-tool :as v.goto]
   [astronomy.view.user.constellation-tool :as v.constellation-tool]
   [astronomy.view.user.atmosphere-tool :as v.atmosphere-tool]
   [astronomy.view.user.horizon-coordinate-tool :as v.horizon-coordinate-tool]
   [astronomy.view.user.terrestrial-coordinate-tool :as v.terrestrial-coordinate-tool]
   [astronomy.view.user.astronomical-point-tool :as v.astronomical-point-tool]
   [astronomy.view.user.ruler-tool :as v.ruler-tool]


   [astronomy.objects.ecliptic.v :as ecliptic.v]
   [astronomy.objects.astronomical-coordinate.v :as astronomical-coordinate.v]

   [astronomy.tools.astronomical-coordinate-tool.v :as astronomical-coordinate-tool.v]
   [astronomy.tools.planet-tool.v :as planet-tool.v]

;; service

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
   [astronomy.service.goto-celestial-tool :as s.goto-tool]
   [astronomy.service.contellation-tool :as s.constellation-tool]
   [astronomy.service.atmosphere-tool :as s.atmosphere-tool]
   [astronomy.service.horizon-coordinate-tool :as s.horizon-coordinate]
   [astronomy.service.terrestrial-coordinate-tool :as s.terrestrial-coordinate-tool]
   [astronomy.service.astronomical-point-tool :as s.astronomical-point-tool]
   [astronomy.service.ruler-tool :as s.ruler-tool]

   [astronomy.objects.planet.h :as planet.h]
   [astronomy.objects.ecliptic.h :as ecliptic.h]

   [astronomy.tools.astronomical-coordinate-tool.h :as astronomical-coordinate-tool.h]
   [astronomy.tools.planet-tool.h :as planet-tool.h]
  
   ;;  
   ))


;; view library

(def tool-library
  {:clock-tool v.clock-tool/ClockToolView
   :info-tool v.info-tool/InfoToolView
   :universe-tool v.universe-tool/UniverseToolView
   :spaceship-camera-control v.spaceship/SpaceshipCameraToolView
   :goto-celestial-tool v.goto/GotoCelestialToolView
   :constellation-tool v.constellation-tool/ConstellationToolView
   :atmosphere-tool v.atmosphere-tool/AtmosphereToolView
   :horizon-coordinate-tool v.horizon-coordinate-tool/HorizonCoordinateToolView
   :astronomical-coordinate-tool astronomical-coordinate-tool.v/AstronomicalCoordinateToolView
   :terrestrial-coordinate-tool v.terrestrial-coordinate-tool/TerrestrialCoordinateToolView
   :astronomical-point-tool v.astronomical-point-tool/AstronomicalPointToolView
   :ruler-tool v.ruler-tool/RulerToolView
   :planet-tool planet-tool.v/PlanetToolView})


(def object-libray 
  {:star v.star/StarView
   :galaxy v.galaxy/GalaxyView
   :terrestrial-coordinate v.terrestrial-coordinate/TerrestrialCoordinateView
   :astronomical-coordinate astronomical-coordinate.v/AstronomicalCoordinateView
   :horizon-coordinate v.horizon-coordinate/HorizonCoordinateView
   :constellation v.constel/ConstellationView
   :astronomical-point-tool v.astronomical-point-tool/AstronomicalPointToolObjectView
   :ruler-tool v.ruler-tool/RulerSceneView
   :ecliptic ecliptic.v/EclipticSceneView})


(def hud-library
  {:ppt-tool v.ppt-tool/PPTHudView
  ;;  :astronomical-point-tool v.astronomical-point-tool/AstronomicalPointHudView
   })


;; service library


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
   #:process{:name "goto-celestial-tool"
             :listen ["goto-celestial-tool"]
             :service-fn s.goto-tool/init-service!}
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
   #:process{:name "camera"
             :listen []
             :service-fn s.camera/init-service!}
   #:process{:name "mouse"
             :listen ["mouse"]
             :service-fn s.mouse/init-service!}])


;; integrant


(derive ::conn :circuit/conn)
(derive ::dom-atom :circuit/atom)
(derive ::state-atom :circuit/atom)
(derive ::view :circuit/view)
(derive ::chan :circuit/chan)
(derive ::service :circuit/service)

(derive ::meta-atom :circuit/ratom)
(derive ::meta-chan :circuit/chan)
(derive ::meta-service :circuit/service)


(defn create-system! [props]
  (let [conn-config (cond
                      (:initial-db props) #:conn {:initial-db (:initial-db props)}
                      (:db-url props) #:conn {:initial-db basic-db
                                              :db-url (:db-url props)}
                      :else #:conn {:initial-db basic-db})
        config {::conn conn-config
                ::dom-atom #:atom {}
                ::state-atom #:atom {}
                ::chan #:chan {}
                ::view #:view {:view-fn RootView
                               :props {:user-name "dr who"
                                       :scene-name "solar"}
                               :env {:conn (ig/ref ::conn)
                                     :service-chan (ig/ref ::chan)
                                     :meta-atom (ig/ref ::meta-atom)
                                     :dom-atom (ig/ref ::dom-atom)
                                     :object-libray object-libray
                                     :tool-library tool-library
                                     :hud-library hud-library}}
                ::service #:service {:service-fn init-service-center!
                                     :props {:user {:db/id [:person/name "dr who"]}
                                             :astro-scene {:db/id [:scene/name "solar"]}
                                             :camera {:db/id [:camera/name "default"]}
                                             :spaceship-camera-control {:db/id [:spaceship-camera-control/name "default"]}}
                                     :env {:service-chan (ig/ref ::chan)
                                           :conn (ig/ref ::conn)
                                           :meta-atom (ig/ref ::meta-atom)
                                           :state-atom (ig/ref ::state-atom)
                                           :dom-atom (ig/ref ::dom-atom)
                                           :processes processes}}
                ::meta-chan #:chan {}
                ::meta-atom #:ratom {:init-value {:mode :read-and-write}}
                ::meta-service #:service{:service-fn init-meta-service!
                                         :props {}
                                         :env {:meta-chan (ig/ref ::meta-chan)
                                               :meta-atom (ig/ref ::meta-atom)
                                               :service-chan (ig/ref ::chan)
                                               :conn (ig/ref ::conn)}}}
        instance (ig/init config)]

    #:system {:conn (::conn instance)
              :view (::view instance)
              :service-chan (::chan instance)
              :dom-atom (::dom-atom instance)
              :meta-atom (::meta-atom instance)
              :meta-chan (::meta-chan instance)}))

