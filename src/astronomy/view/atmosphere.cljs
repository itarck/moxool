(ns astronomy.view.atmosphere
  (:require
   [helix.core :refer [$]]
   [posh.reagent :as p]
   ["@react-three/drei" :refer [Sky]]
   [astronomy.model.atmosphere :as m.atmosphere]
   [astronomy.model.astro-scene :as m.astro-scene]))


(defn AtmosphereView [{:keys [has-atmosphere?]} {:keys [conn] :as env}]
  (let [atmosphere (m.atmosphere/sub-unique-one conn)
        camera-control @(p/pull conn '[*] [:spaceship-camera-control/name "default"])
        sun-position (m.atmosphere/sun-position-vector atmosphere)
        {:spaceship-camera-control/keys [up]} camera-control]
    (when has-atmosphere?
     ($ Sky {:distance 500
             :rayleigh 1
             :sunPosition sun-position
             :mieCoefficient 0.05
             :mieDirectionalG 0.9999
             :material-uniforms-up-value (clj->js up)}))))