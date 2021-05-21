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
        sun @(p/pull conn '[*] [:star/name "sun"])
        sun-position (v3/apply-matrix4 (v3/from-seq (:star/position sun)) (m.coordinate/cal-invert-matrix coor-1))
        {:spaceship-camera-control/keys [up]} camera-control]
    (when (= :surface-control (:spaceship-camera-control/mode camera-control))
      ($ Sky {:distance 500
              :sunPosition sun-position
              :mieCoefficient 0.05
              :mieDirectionalG 0.9999
              :material-uniforms-up-value (clj->js up)}))))