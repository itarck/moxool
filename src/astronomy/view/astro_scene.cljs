(ns astronomy.view.astro-scene
  (:require
   [posh.reagent :as p]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.coordinate :as m.coordinate]
   [astronomy.model.atmosphere :as m.atmosphere]
   [astronomy.view.background :as v.background]
   [astronomy.view.constellation :as v.constel]
   [astronomy.view.star :as v.star]
   [astronomy.view.atmosphere :as v.atmosphere]
   [astronomy.objects.planet.v :as planet.v]))


(defn AstroSceneView [props {:keys [conn object-libray] :as env}]
  (let [astro-scene @(p/pull conn '[* {:object/_scene [*]
                                       :astro-scene/coordinate [*]}]
                             (get-in props [:astro-scene :db/id]))
        {scale :scene/scale 
         coordiante :astro-scene/coordinate
         objects :object/_scene} astro-scene
        user @(p/pull conn '[*] (get-in props [:user :db/id]))
        atmosphere (m.atmosphere/sub-unique-one conn)
        invert-matrix (m.coordinate/cal-invert-matrix coordiante)
        ;; has-day-light? (m.astro-scene/sub-has-day-light? conn astro-scene)
        ]
    ;; (println "astro scene view mounted ?? " invert-matrix)
    [:<>
     [:mesh {:scale [scale scale scale]}
      [v.atmosphere/AtmosphereView {:object atmosphere} env]

      [planet.v/PlanetsHasPositionLogView props env]

      [:group {:matrixAutoUpdate false
               :matrix invert-matrix}

       [:<>
        [v.constel/ConstellationsView {} env]
        [v.background/BackgroundView {} env]
        [v.star/StarsSphereView {} env]]

       (for [object objects]
         (let [object-view-fn (get object-libray (:entity/type object))]
           (when object-view-fn
             ^{:key (:db/id object)} [object-view-fn {:object object
                                                      :user user
                                                      :astro-scene astro-scene} env])))
      ;;  
       ]]]))