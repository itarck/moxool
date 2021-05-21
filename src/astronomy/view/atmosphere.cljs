(ns astronomy.view.atmosphere
  (:require
   [helix.core :refer [$]]
   [posh.reagent :as p]
   ["@react-three/drei" :refer [Sky]]
   [shu.three.vector3 :as v3]
   [astronomy.model.coordinate :as m.coordinate]))


(defn AtmosphereView [{:keys [camera-control-id] :as props} {:keys [conn] :as env}]
  (let [camera-control @(p/pull conn '[*] camera-control-id)
        coor-1 @(p/pull conn '[*] [:coordinate/name "default"])
        sun-position (m.coordinate/original-position coor-1)
        {:spaceship-camera-control/keys [up]} camera-control
        angle (v3/angle-to (v3/from-seq up) sun-position)]
    (when (and
           (= :surface-control (:spaceship-camera-control/mode camera-control))
           (< angle (* 0.55 Math/PI)))
      ($ Sky {:distance 500
              :rayleigh 1
              :sunPosition sun-position
              :mieCoefficient 0.05
              :mieDirectionalG 0.9999
              :material-uniforms-up-value (clj->js up)}))))