(ns astronomy.view.astro-scene
  (:require
   [posh.reagent :as p]
   [methodology.model.scene :as m.scene]
   [astronomy.model.coordinate :as m.coordinate]
   [astronomy.view.star :as v.star]
   [astronomy.view.background :as v.background]
   [astronomy.view.celestial-sphere-helper :refer [CelestialSphereHelperView]]
   [astronomy.view.galaxy :as v.galaxy]
   [astronomy.view.constellation :as v.constel]))


(defn AstroSceneView [entity env]
  (let [{:keys [conn]} env
        astro-scene @(p/pull conn '[* {:object/_scene [*]}] (:db/id entity))
        ref @(p/pull conn '[*] [:coordinate/name "default"])
        {:scene/keys [scale]} astro-scene
        objects (m.scene/sub-objects conn (:db/id astro-scene))]
    ;; (println "scene view mounted" )
    [:<>
     [:mesh {:scale [scale scale scale]}
      [:mesh {:matrixAutoUpdate false
              :matrix (m.coordinate/cal-invert-matrix ref)}
       #_[CelestialSphereHelperView 1000000]
       #_[v.constel/ConstellationsView {} env]
       [v.background/BackgroundView]
       (for [object objects]
         (case (:entity/type object)
           :star ^{:key (:db/id object)} [v.star/StarView object env]
           :galaxy ^{:key (:db/id object)} [v.galaxy/GalaxyView object env]
           nil))]
      
      #_[:PolarGridHelper {:args #js [10 4 10 360 "gray" "gray"]}]
      ]]
    ))