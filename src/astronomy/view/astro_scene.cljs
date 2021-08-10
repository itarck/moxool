(ns astronomy.view.astro-scene
  (:require
   [posh.reagent :as p]
   [methodology.model.scene :as m.scene]
   [methodology.model.object :as m.object]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.atmosphere :as m.atmosphere]
   [astronomy.view.background :as v.background]
   [astronomy.view.constellation :as v.constel]
   [astronomy.view.star :as v.star]
   [astronomy.view.atmosphere :as v.atmosphere]))


(defn AstroSceneView [props {:keys [conn object-libray] :as env}]
  (let [astro-scene @(p/pull conn '[*] (get-in props [:astro-scene :db/id]))
        user @(p/pull conn '[*] (get-in props [:user :db/id]))
        atmosphere (m.atmosphere/sub-unique-one conn)
        {:scene/keys [scale]} astro-scene
        objects (m.scene/sub-objects conn (:db/id astro-scene))
        coor @(p/pull conn '[*] (get-in astro-scene [:astro-scene/coordinate :db/id]))
        invert-matrix (m.object/cal-invert-matrix coor)]
    ;; (println "astro scene view mounted ?? " invert-matrix)
    [:<>
     [:mesh {:scale [scale scale scale]}
      [v.atmosphere/AtmosphereView {:object atmosphere} env]

      [:group {:matrixAutoUpdate false
               :matrix invert-matrix}

       [v.constel/ConstellationsView {:astro-scene astro-scene} env]
       [v.background/BackgroundView {:astro-scene astro-scene} env]
       [v.star/StarsSphereView {:astro-scene astro-scene} env]

       (for [object objects]
         (let [object-view-fn (get object-libray (:entity/type object))]
           (when object-view-fn
             ^{:key (:db/id object)} [object-view-fn {:object object
                                                      :user user
                                                      :astro-scene astro-scene} env])))
      ;;  
       ]]]))