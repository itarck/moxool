(ns astronomy.view.astro-scene
  (:require
   [posh.reagent :as p]
   [helix.core :refer [$]]
   [methodology.model.scene :as m.scene]
   [shu.three.vector3 :as v3]
   [astronomy.model.astro-scene :as m.astro-scene]
   [methodology.model.object :as m.object]
   [astronomy.model.atmosphere :as m.atmosphere]
   [astronomy.view.background :as v.background]
   [astronomy.view.constellation :as v.constel]
   [astronomy.view.star :as v.star]
   [astronomy.view.atmosphere :as v.atmosphere]
   ))


(defn AstroSceneView [props {:keys [conn object-libray] :as env}]
  (let [astro-scene @(p/pull conn '[*] (get-in props [:astro-scene :db/id]))
        user @(p/pull conn '[*] (get-in props [:user :db/id]))
        atmosphere (m.atmosphere/sub-unique-one conn)
        {:scene/keys [scale]} astro-scene
        objects (m.scene/sub-objects conn (:db/id astro-scene))
        spaceship-camera-control @(p/pull conn '[*] (get-in props [:spaceship-camera-control :db/id]))
        coor @(p/pull conn '[*] (get-in astro-scene [:astro-scene/coordinate :db/id]))
        invert-matrix (m.object/cal-invert-matrix coor)
        sun-position (v3/apply-matrix4 (v3/vector3 0 0 0) invert-matrix)
        has-day-light? (m.astro-scene/has-day-light? sun-position spaceship-camera-control atmosphere 0.5)
        has-atmosphere? (m.astro-scene/has-day-light? sun-position spaceship-camera-control atmosphere 0.55)]
    ;; (println "astro scene view mounted ?? " invert-matrix)
    [:<>
     [:mesh {:scale [scale scale scale]}
      [v.atmosphere/AtmosphereView {:has-atmosphere? has-atmosphere?
                                    :sun-position sun-position
                                    :up (:spaceship-camera-control/up spaceship-camera-control)} env]

      [:group {:matrixAutoUpdate false
               :matrix invert-matrix}

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
               ^{:key (:db/id object)} [object-view-fn {:object object
                                                        :user user
                                                        :astro-scene astro-scene} env]))))]

      #_[:PolarGridHelper {:args [10 4 10 360 "red" "red"]}]]]
    ))