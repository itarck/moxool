(ns astronomy.view.astro-scene
  (:require
   [posh.reagent :as p]
   [helix.core :refer [$]]
   [methodology.model.scene :as m.scene]
   [shu.three.vector3 :as v3]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.coordinate :as m.coordinate]
   [astronomy.view.background :as v.background]
   [astronomy.view.constellation :as v.constel]
   [astronomy.view.star :as v.star]
   [astronomy.model.atmosphere :as m.atmosphere]
   [astronomy.view.atmosphere :as v.atmosphere]))


(defn AstroSceneView [props {:keys [conn object-libray] :as env}]
  (let [astro-scene @(p/pull conn '[*] (get-in props [:astro-scene :db/id]))
        atmosphere (m.atmosphere/sub-unique-one conn)
        {:scene/keys [scale]} astro-scene
        objects (m.scene/sub-objects conn (:db/id astro-scene))
        spaceship-camera-control @(p/pull conn '[*] (get-in props [:spaceship-camera-control :db/id]))
        coor-1 @(p/pull conn '[*] (get-in astro-scene [:astro-scene/coordinate :db/id]))
        has-day-light? (m.astro-scene/has-day-light? coor-1 spaceship-camera-control atmosphere 0.5)
        has-atmosphere? (m.astro-scene/has-day-light? coor-1 spaceship-camera-control atmosphere 0.55)]
    ;; (println "scene view mounted ?? ")
    [:<>
     [:mesh {:scale [scale scale scale]}
      [v.atmosphere/AtmosphereView {:has-atmosphere? has-atmosphere?} env]

      [:group {:matrixAutoUpdate false
               :matrix (m.coordinate/cal-invert-matrix coor-1)}

       [v.constel/ConstellationsView {:has-day-light? has-day-light?} env]

       (when-not has-day-light?
         [v.background/BackgroundView])

       [v.star/StarsSphereView {:has-day-light? has-day-light?} env]


       (for [object objects]
         (let [object-view-fn (get object-libray (:entity/type object))]
           (when object-view-fn
             (case (:entity/type object)
               :star ^{:key (:db/id object)} [object-view-fn {:object object
                                                              :has-day-light? has-day-light?
                                                              :astro-scene astro-scene} env]
               :horizontal-coordinate  ^{:key (:db/id object)} [object-view-fn {:object object
                                                                                :spaceship-camera-control spaceship-camera-control
                                                                                :astro-scene astro-scene} env]
               ^{:key (:db/id object)} [object-view-fn {:object object} env]))))]

      #_[:PolarGridHelper {:args [10 4 10 360 "red" "red"]}]]]
    ))