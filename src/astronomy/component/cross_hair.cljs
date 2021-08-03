(ns astronomy.component.cross-hair
  (:require
   [shu.three.vector3 :as v3]
   [helix.core :refer [defnc $] :as h]
   ["react" :as react :refer [Suspense]]
   ["@react-three/drei" :refer [Html Billboard useTexture]]))


(defnc CrossHairComponent [props]
  (let [{:keys [position onClick]} props
        size (* 0.005 (v3/length (v3/from-seq (seq position))))
        texture1 (useTexture "/image/moxool/crosshair1.png")]
    ($ Billboard {:args #js [size size]
                  :position position
                  :onClick onClick}
       ($ :meshStandardMaterial {:map texture1 :attach "material"}))))
