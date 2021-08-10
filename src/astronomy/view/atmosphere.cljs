(ns astronomy.view.atmosphere
  (:require
   [helix.core :refer [$]]
   [posh.reagent :as p]
   ["@react-three/drei" :refer [Sky]]
   [shu.three.vector3 :as v3]
   [astronomy.model.atmosphere :as m.atmosphere]))


(defn AtmosphereView [{:keys [sun-position object]} {:keys [conn] :as env}]
  (let [show-atmosphere? (m.atmosphere/sub-show-atmosphere? conn object)]
    (when show-atmosphere?
      ($ Sky {:distance 500
              :rayleigh 1
              :sunPosition (v3/from-seq sun-position)
              :mieCoefficient 0.05
              :mieDirectionalG 0.9999
              :material-uniforms-up-value #js [0 1 0]}))))