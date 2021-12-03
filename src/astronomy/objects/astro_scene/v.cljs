(ns astronomy.objects.astro-scene.v
  (:require
   [posh.reagent :as p]
   [astronomy.objects.astro-scene.m :as m.astro-scene]
   [astronomy.objects.coordinate.m :as m.coordinate]
   [astronomy.objects.atmosphere.m :as m.atmosphere]
   [astronomy.objects.background.v :as v.background]
   [astronomy.objects.constellation.v :as v.constel]
   [astronomy.objects.star.v :as v.star]
   [astronomy.objects.atmosphere.v :as v.atmosphere]
   [astronomy.objects.planet.v :as planet.v]))


(defn AstroSceneView [props {:keys [conn object-libray] :as env}]
  (let [astro-scene @(p/pull conn '[* {:object/_scene [*]
                                       :astro-scene/coordinate [*]}]
                             (get-in props [:astro-scene :db/id]))
        {scale :scene/scale
         coordiante :astro-scene/coordinate
         objects :object/_scene} astro-scene
        user @(p/pull conn '[*] (get-in props [:user :db/id]))
        ;; atmosphere (m.atmosphere/sub-unique-one conn)
        invert-matrix (m.coordinate/cal-origin-invert-matrix-now coordiante)
        ;; has-day-light? (m.astro-scene/sub-has-day-light? conn astro-scene)
        ]
    ;; (println "astro scene view mounted ?? " invert-matrix)
    [:<>
     
     [:ambientLight {:intensity (:scene/ambient-light-intensity astro-scene)}]

     [:mesh {:scale [scale scale scale]}
      ;; [v.atmosphere/AtmosphereView {:object atmosphere} env]

      [planet.v/PlanetsHasPositionLogView props env]

      [:group {:matrixAutoUpdate false
               :matrix invert-matrix}

       [:<>
        ;; [v.constel/ConstellationsView {} env]
        ;; [v.background/BackgroundView {} env]
        [v.star/SavedStarsSphereView {} env]]


       (for [object objects]
         (let [object-view-fn (get object-libray (:entity/type object))]
           (when object-view-fn
             ^{:key (:db/id object)} [object-view-fn {:object object
                                                      :user user
                                                      :astro-scene astro-scene} env])))
      ;;  
       ]]]))