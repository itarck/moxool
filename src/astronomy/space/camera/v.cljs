(ns astronomy.space.camera.v
  (:require
   [applied-science.js-interop :as j]
   [cljs-bean.core :refer [->clj ->js]]
   [posh.reagent :as p]
   [helix.core :as helix :refer [$ defnc]]
   ["react-three-fiber" :refer [useThree]]
   ["@react-three/drei" :refer [PerspectiveCamera]]))



(defnc CameraComponent [props]
  (let [three-instance (useThree)
        {:keys [camera scene]} (->clj three-instance)
        {:keys [domAtom position quaternion far near]} (->clj props)]
    (when domAtom
      (swap! domAtom assoc :camera camera)
      (swap! domAtom assoc :three-instance three-instance)
      (swap! domAtom assoc :scene scene))
    (println "camera mounted" (keys @domAtom))
    ($ PerspectiveCamera {:makeDefault true
                          ;; :args #js [50]
                          :near (or near 0.001)
                          :far (or far 50000000000000000000)
                          :position (clj->js position)
                          :quaternion (clj->js quaternion)})))

(defn CameraView [props env]
  (let [{:keys [conn]} env
        camera @(p/pull conn '[*] (:db/id props))
        {:camera/keys [position quaternion far near]} camera]
    (fn [props {:keys [dom-atom]}]
      (println "! camera remonted")
      ($ CameraComponent {:position position
                          :quaternion quaternion
                          :far far
                          :near near
                          :domAtom dom-atom}))))

