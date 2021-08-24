(ns astronomy.component.gltf
  (:require
   [applied-science.js-interop :as j]
   [helix.core :as h :refer [defnc $]]
   [helix.hooks :refer [use-memo]]
   ["@react-three/drei" :refer [useGLTF]]))


(h/defnc GLTF [props]
  (let [gltf (useGLTF (:url props))
        scene (j/get gltf :scene)
        copied-scene (use-memo [scene]
                               (j/call scene :clone))]

    ($ :primitive {:object copied-scene
                   :dispose nil})))
