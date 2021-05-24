(ns astronomy.view.astro-scene
  (:require
   [posh.reagent :as p]
   [helix.core :refer [$]]
   [methodology.model.scene :as m.scene]
   [shu.three.vector3 :as v3]
   [astronomy.model.coordinate :as m.coordinate]
   [astronomy.model.constellation :as m.constel]
   [astronomy.view.star :as v.star]
   [astronomy.view.background :as v.background]
   [astronomy.view.celestial-sphere-helper :refer [CelestialSphereHelperView]]
   [astronomy.view.galaxy :as v.galaxy]
   [astronomy.view.constellation :as v.constel]
   [astronomy.view.atmosphere :as v.atmosphere]))


(defn AstroSceneView [props {:keys [conn] :as env}]
  (let [{:keys [astro-scene-id camera-control-id]} props
        astro-scene @(p/pull conn '[* {:object/_scene [*]}] astro-scene-id)
        {:scene/keys [scale]} astro-scene
        objects (m.scene/sub-objects conn (:db/id astro-scene))

        camera-control @(p/pull conn '[*] camera-control-id)
        coor-1 @(p/pull conn '[*] [:coordinate/name "default"])
        sun-position (m.coordinate/original-position coor-1)
        {:spaceship-camera-control/keys [up]} camera-control
        angle (v3/angle-to (v3/from-seq up) sun-position)
        has-day-light? (and
                        (= :surface-control (:spaceship-camera-control/mode camera-control))
                        (< angle (* 0.5 Math/PI)))]
    ;; (println "scene view mounted" )
    [:<>
     [:mesh {:scale [scale scale scale]}
      [v.atmosphere/AtmosphereView props env]
      
      [:mesh {:matrixAutoUpdate false
              :matrix (m.coordinate/cal-invert-matrix coor-1)}
      ;;  [CelestialSphereHelperView 31536000]
      ;;  [v.constel/ConstellationsView {} env]

       (when-not has-day-light?
         [v.background/BackgroundView])
       
      ;;  [v.background/StarsProjectionComponent {:stars (m.constel/sub-all-constellation-stars conn)} env]

       (for [object objects]
         (case (:entity/type object)
           :star ^{:key (:db/id object)} [v.star/StarView object env]
           :galaxy ^{:key (:db/id object)} [v.galaxy/GalaxyView object env]
           nil))]
      
      
      
      #_[:PolarGridHelper {:args #js [10 4 10 360 "gray" "gray"]}]
      ]]
    ))