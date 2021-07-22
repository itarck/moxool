(ns astronomy.system.solar
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [astronomy.service.meta :refer [init-meta-service!]]
   [astronomy.service.core :refer [init-service-center!]]
   [astronomy.view.core :refer [RootView]]
   [astronomy.model.core :refer [basic-db]]

   [astronomy.view.star :as v.star]
   [astronomy.view.galaxy :as v.galaxy]
   [astronomy.view.constellation :as v.constel]
   [astronomy.view.horizontal-coordinate :as v.horizon]
   [astronomy.view.terrestrial-coordinate :as v.terrestrial-coordinate]
   [astronomy.view.astronomical-coordinate :as v.astronomical-coordinate]
   [astronomy.view.horizon-coordinate :as v.horizon-coordinate]
   
   [astronomy.view.user.universe-tool :as v.universe-tool]
   [astronomy.view.user.clock-tool :as v.clock-tool]
   [astronomy.view.user.spaceship-camera-control :as v.spaceship]
   [astronomy.view.user.info-tool :as v.info-tool]
   [astronomy.view.user.ppt-tool :as v.ppt-tool]
   [astronomy.view.user.horizontal-coordinate-tool :as v.horizon-tool]
   [astronomy.view.user.goto-celestial-tool :as v.goto]
   [astronomy.view.user.equatorial-coordinate-tool :as v.ect]
   [astronomy.view.user.constellation-tool :as v.constellation-tool]
   [astronomy.view.user.atmosphere-tool :as v.atmosphere-tool]
   [astronomy.view.user.eagle-eye-tool :as v.eagle-eye]
   [astronomy.view.user.horizon-coordinate-tool :as v.horizon-coordinate-tool]
   ))



(derive ::conn :circuit/conn)
(derive ::dom-atom :circuit/atom)
(derive ::state-atom :circuit/atom)
(derive ::view :circuit/view)
(derive ::chan :circuit/chan)
(derive ::service :circuit/service)

(derive ::meta-atom :circuit/ratom)
(derive ::meta-chan :circuit/chan)
(derive ::meta-service :circuit/service)


(def tool-library
  {:clock-tool v.clock-tool/ClockToolView
   :info-tool v.info-tool/InfoToolView
   :universe-tool v.universe-tool/UniverseToolView
   :spaceship-camera-control v.spaceship/SpaceshipCameraToolView
   :ppt-tool v.ppt-tool/PPTToolView
   :horizontal-coordinate-tool v.horizon-tool/HorizontalCoordinateToolView
   :goto-celestial-tool v.goto/GotoCelestialToolView
   :equatorial-coordinate-tool v.ect/EquatorialCoordinateToolView
   :constellation-tool v.constellation-tool/ConstellationToolView
   :atmosphere-tool v.atmosphere-tool/AtmosphereToolView
   :eagle-eye-tool v.eagle-eye/EagleEyeToolView
   :horizon-coordinate-tool v.horizon-coordinate-tool/HorizonCoordinateToolView})


(def object-libray 
  {:star v.star/StarView
   :galaxy v.galaxy/GalaxyView
   :terrestrial-coordinate v.terrestrial-coordinate/TerrestrialCoordinateView
   :astronomical-coordinate v.astronomical-coordinate/AstronomicalCoordinateView
   :horizon-coordinate v.horizon-coordinate/HorizonCoordinateView
   :horizontal-coordinate v.horizon/HorizontalCoordinateSceneView
   :equatorial-coordinate-tool v.ect/EquatorialCoordinateSceneView
   :constellation v.constel/ConstellationView})


(defn create-system! [props]
  (let [db-url (or (get props :db-url) "/edn/free-mode.edn")
        config {::conn #:conn {:initial-db basic-db
                               :db-url db-url}
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
                                     :tool-library tool-library}}
                ::service #:service {:service-fn init-service-center!
                                     :props {:user {:db/id [:person/name "dr who"]}
                                             :astro-scene {:db/id [:scene/name "solar"]}
                                             :camera {:db/id [:camera/name "default"]}
                                             :spaceship-camera-control {:db/id [:spaceship-camera-control/name "default"]}}
                                     :env {:service-chan (ig/ref ::chan)
                                           :conn (ig/ref ::conn)
                                           :meta-atom (ig/ref ::meta-atom)
                                           :state-atom (ig/ref ::state-atom)
                                           :dom-atom (ig/ref ::dom-atom)}}
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

