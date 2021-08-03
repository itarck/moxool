(ns astronomy.component.cross-hair
  (:require
   [shu.three.vector3 :as v3]
   [helix.core :refer [defnc $] :as h]
   ["react" :as react :refer [Suspense]]
   ["@react-three/drei" :refer [Html Billboard useTexture]]))


(defnc CrossHairComponent [props]
  (let [{:keys [position]} props
        size (* 0.005 (v3/length (v3/from-seq (seq position))))
        texture1 (useTexture "/image/moxool/crosshair1.png")]
    ($ Suspense {:fallback nil}
       ($ Billboard {:args #js [size size]
                     :position position}
          ($ :meshStandardMaterial {:map texture1 :attach "material"})))))