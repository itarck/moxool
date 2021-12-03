(ns astronomy.parts.root-view
  (:require
   ["react-three-fiber" :refer [Canvas]]
   [posh.reagent :as p]
   [astronomy.space.user.m :as m.person]
   [astronomy.objects.astro-scene.m :as m.astro-scene]
   [astronomy.objects.astro-scene.v :as v.astro-scene]
   [astronomy.space.camera.v :as v.camera]
   [astronomy.space.user.v :as v.user]

     ;;  views

   [astronomy.objects.star.v :as star.v]
   [astronomy.objects.constellation.v :as v.constel]
   [astronomy.objects.ecliptic.v :as ecliptic.v]
   [astronomy.objects.astronomical-coordinate.v :as astronomical-coordinate.v]
   [astronomy.objects.terrestrial-coordinate.v :as terrestrial-coordinate.v]
   [astronomy.objects.horizon-coordinate.v :as horizon-coordinate.v]
   [astronomy.objects.galaxy.v :as v.galaxy]

   [astronomy.tools.universe-tool.v :as v.universe-tool]
   [astronomy.tools.info-tool.v :as v.info-tool]
   [astronomy.tools.constellation-tool.v :as v.constellation-tool]
   [astronomy.tools.atmosphere-tool.v :as v.atmosphere-tool]
   [astronomy.tools.horizon-coordinate-tool.v :as horizon-coordinate.v-tool]
   [astronomy.tools.terrestrial-coordinate-tool.v :as v.terrestrial-coordinate-tool]
   [astronomy.tools.ruler-tool.v :as v.ruler-tool]
   [astronomy.tools.ppt-tool.v :as v.ppt-tool]
   [astronomy.tools.clock-tool.v :as v.clock-tool]
   [astronomy.tools.astronomical-coordinate-tool.v :as astronomical-coordinate-tool.v]
   [astronomy.tools.astronomical-point-tool.v :as v.astronomical-point-tool]
   [astronomy.tools.spaceship-camera-control.v :as v.spaceship]
   [astronomy.tools.planet-tool.v :as planet-tool.v]
   [astronomy.tools.satellite-tool.v :as satellite-tool.v]
   [astronomy.tools.ellipse-orbit-tool.v :as ellipse-orbit-tool.v]))


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
   :satellite-tool satellite-tool.v/SatelliteToolView
   :ellipse-orbit-tool ellipse-orbit-tool.v/EllipseOrbitToolView})


(def object-libray
  {:star star.v/StarView
   :galaxy v.galaxy/GalaxyView
   :terrestrial-coordinate terrestrial-coordinate.v/TerrestrialCoordinateView
   :astronomical-coordinate astronomical-coordinate.v/AstronomicalCoordinateView
   :horizon-coordinate horizon-coordinate.v/HorizonCoordinateView
   :constellation v.constel/ConstellationView
   :astronomical-point-tool v.astronomical-point-tool/AstronomicalPointToolObjectView
   :ruler-tool v.ruler-tool/RulerSceneView
   :ecliptic ecliptic.v/EclipticSceneView})


(def hud-library
  {:ppt-tool v.ppt-tool/PPTHudView
  ;;  :astronomical-point-tool v.astronomical-point-tool/AstronomicalPointHudView
   })



(defn RootView [props env]
  (let [{:keys [user-name scene-name]} props
        astro-scene {:db/id [:scene/name scene-name]}
        user {:db/id [:user/name user-name]}
        {:keys [meta-atom conn]} env
        mode (if meta-atom (:mode @meta-atom) :read-and-write)]
    ;; (println "load root view ???")
    [:<>
     [:> Canvas {:style {:background :black
                         :style {:height "100%"
                                 :width "100%"}}
                 :shadowMap true}
      (when (m.astro-scene/sub-scene-name-exist? conn scene-name)
        (let [astro-scene-1 @(p/pull conn '[*] [:scene/name scene-name])
              user-1 @(p/pull conn '[*] (:db/id user))
              spaceship-camera-control (:user/camera-control user-1)]
          [:<>
           [v.camera/CameraView (:astro-scene/camera astro-scene-1) env]
           [v.astro-scene/AstroSceneView {:astro-scene astro-scene
                                          :user user
                                          :spaceship-camera-control spaceship-camera-control} env]
           (when (= mode :read-and-write)
             [v.spaceship/SpaceshipCameraControlView {:spaceship-camera-control spaceship-camera-control
                                                      :astro-scene astro-scene-1} env])

           #_[:gridHelper {:args [10000 100 "gray" "gray"]}]]))]

     (when (m.person/sub-user-name-exist? conn user-name)
       (let [user-1 @(p/pull conn '[*] (:db/id user))
             astro-scene-1 @(p/pull conn '[*] [:scene/name scene-name])
             spaceship-camera-control (:user/camera-control user-1)]
         [v.user/UserView {:user user
                             :astro-scene astro-scene-1
                             :camera-control spaceship-camera-control} env]))]))


