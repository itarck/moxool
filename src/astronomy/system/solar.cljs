(ns astronomy.system.solar
  (:require
   [datascript.core :as d]
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [astronomy.conn.schema :refer [schema]]
   [astronomy.service.meta :refer [init-meta-service!]]
   [astronomy.service.core :refer [init-service-center!]]
   [astronomy.ig.root-view :refer [RootView]]

  ;;  views
   [astronomy.view.user.universe-tool :as v.universe-tool]
   [astronomy.view.user.info-tool :as v.info-tool]
   [astronomy.view.user.constellation-tool :as v.constellation-tool]
   [astronomy.view.user.atmosphere-tool :as v.atmosphere-tool]
   [astronomy.view.user.horizon-coordinate-tool :as horizon-coordinate.v-tool]
   [astronomy.view.user.terrestrial-coordinate-tool :as v.terrestrial-coordinate-tool]
   [astronomy.view.user.ruler-tool :as v.ruler-tool]

   [astronomy.objects.constellation.v :as v.constel]
   [astronomy.objects.star.v :as star.v]
   [astronomy.objects.ecliptic.v :as ecliptic.v]
   [astronomy.objects.astronomical-coordinate.v :as astronomical-coordinate.v]
   [astronomy.objects.terrestrial-coordinate.v :as terrestrial-coordinate.v]
   [astronomy.objects.horizon-coordinate.v :as horizon-coordinate.v]
   [astronomy.objects.galaxy.v :as galaxy.v]

   [astronomy.tools.ppt-tool.v :as v.ppt-tool]
   [astronomy.tools.clock-tool.v :as v.clock-tool]
   [astronomy.tools.spaceship-camera-control.v :as v.spaceship]
   [astronomy.tools.astronomical-coordinate-tool.v :as astronomical-coordinate-tool.v]
   [astronomy.tools.astronomical-point-tool.v :as v.astronomical-point-tool]
   [astronomy.tools.planet-tool.v :as planet-tool.v]
   [astronomy.tools.satellite-tool.v :as satellite-tool.v]

;; service

   [methodology.service.camera :as s.camera]
   [methodology.service.mouse :as s.mouse]
   [astronomy.service.keyboard-listener :as s.keyboard-listener]
   [astronomy.service.tool :as s.tool]
   [astronomy.service.user :as s.user]
   [astronomy.service.astro-scene :as s.astro-scene]
   [astronomy.service.universe-tool :as s.universe-tool]
   [astronomy.service.info-tool :as s.info-tool]
   [astronomy.service.contellation-tool :as s.constellation-tool]
   [astronomy.service.atmosphere-tool :as s.atmosphere-tool]
   [astronomy.service.horizon-coordinate-tool :as s.horizon-coordinate]
   [astronomy.service.terrestrial-coordinate-tool :as s.terrestrial-coordinate-tool]
   [astronomy.service.ruler-tool :as s.ruler-tool]

   [astronomy.objects.planet.h :as planet.h]
   [astronomy.objects.satellite.h :as satellite.h]
   [astronomy.objects.ecliptic.h :as ecliptic.h]

   [astronomy.tools.astronomical-coordinate-tool.h :as astronomical-coordinate-tool.h]
   [astronomy.tools.astronomical-point-tool.h :as s.astronomical-point-tool]
   [astronomy.tools.spaceship-camera-control.s :as s.spaceship]
   [astronomy.tools.clock-tool.s :as s.clock-tool]
   [astronomy.tools.planet-tool.h :as planet-tool.h]
   [astronomy.tools.satellite-tool.h :as satellite-tool.h]
   [astronomy.tools.ppt-tool.h :as s.ppt-tool]
  
   ;;  
   ))


;; view library

(def tool-library
  {:clock-tool v.clock-tool/ClockToolView
   :info-tool v.info-tool/InfoToolView
   :universe-tool v.universe-tool/UniverseToolView
   :spaceship-camera-control v.spaceship/SpaceshipCameraToolView
   :constellation-tool v.constellation-tool/ConstellationToolView
   :atmosphere-tool v.atmosphere-tool/AtmosphereToolView
   :horizon-coordinate-tool horizon-coordinate.v-tool/HorizonCoordinateToolView
   :astronomical-coordinate-tool astronomical-coordinate-tool.v/AstronomicalCoordinateToolView
   :terrestrial-coordinate-tool v.terrestrial-coordinate-tool/TerrestrialCoordinateToolView
   :astronomical-point-tool v.astronomical-point-tool/AstronomicalPointToolView
   :ruler-tool v.ruler-tool/RulerToolView
   :planet-tool planet-tool.v/PlanetToolView
   :satellite-tool satellite-tool.v/SatelliteToolView})


(def object-libray 
  {:star star.v/StarView
   :galaxy galaxy.v/GalaxyView
   :terrestrial-coordinate terrestrial-coordinate.v/TerrestrialCoordinateView
   :astronomical-coordinate astronomical-coordinate.v/AstronomicalCoordinateView
   :horizon-coordinate horizon-coordinate.v/HorizonCoordinateView
   :constellation v.constel/ConstellationView
   :astronomical-point-tool v.astronomical-point-tool/AstronomicalPointToolObjectView
   :ruler-tool v.ruler-tool/RulerSceneView
   :ecliptic ecliptic.v/EclipticSceneView
   })


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
   #:process{:name "satellite"
             :listen ["satellite"]
             :handle-event-fn satellite.h/handle-event}
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


(def basic-db
  (let [conn (d/create-conn schema)]
    @conn))


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

