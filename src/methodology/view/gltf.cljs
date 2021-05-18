(ns methodology.view.gltf
  (:require
   [applied-science.js-interop :as j]
   [posh.reagent :as p]
   [helix.core :refer [defnc $]]
   [helix.hooks :refer [use-memo]]
   ["@react-three/drei" :refer [useGLTF]]
   ["react" :as react :refer [useRef Suspense]]))


(defnc Model [props]
  (let [gltf (useGLTF (:url props))
        scene (j/get gltf :scene)
        copied-scene (use-memo [scene]
                               (j/call scene :clone))]

    ($ :primitive {:object copied-scene
                   :dispose nil})))


(defn GltfView [props env]
  (let [gltf @(p/pull (:conn env) '[*] (:db/id props))
        {:gltf/keys [url scale position]} gltf]
    [:mesh {:scale (or scale [1 1 1])
            :position (or position [0 0 0])}
     [:> Suspense {:fallback nil}
      [:> Model {:url url}]]]))