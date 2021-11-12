(ns astronomy.component.cross-hair
  (:require
   [shu.three.vector3 :as v3]
   [helix.core :refer [defnc $] :as h]
   ["three" :as three]
   ["react" :as react :refer [Suspense]]
   ["@react-three/drei" :refer [Html Billboard useTexture]]))


(defnc CrossHairComponent [props]
  (let [{:keys [position onClick size]} props
        length (v3/length (v3/from-seq (seq position)))
        board-size (* 2 Math/PI length 1.5 (/ 1 360) size) 
        texture1 (useTexture "image/moxool/crosshair.png")]
    ($ Billboard {:args #js [board-size board-size]
                  :position position
                  :onClick onClick}
       ($ :meshStandardMaterial
          {:map texture1
           :side three/DoubleSide
           :attach "material"}))))

