(ns astronomy.objects.atmosphere.v
  (:require
   [helix.core :refer [$]]
   [posh.reagent :as p]
   ["@react-three/drei" :refer [Sky]]
   [shu.three.vector3 :as v3]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.objects.atmosphere.m :as m.atmosphere]
   [astronomy.objects.horizon-coordinate.m :as hc.m]))


(defn AtmosphereView [{:keys [object]} {:keys [conn] :as env}]
  (let [atmosphere @(p/pull conn '[*] (:db/id object))
        coordinate (m.astro-scene/sub-scene-coordinate conn (:object/scene atmosphere))
        show-atmosphere? (m.atmosphere/sub-show-atmosphere? conn object)
        sun-position (hc.m/get-sun-position coordinate)]
    (when show-atmosphere?
      ($ Sky {:distance 500
              :rayleigh 1
              :sunPosition (v3/from-seq sun-position)
              :mieCoefficient 0.05
              :mieDirectionalG 0.9999
              :material-uniforms-up-value #js [0 1 0]}))))