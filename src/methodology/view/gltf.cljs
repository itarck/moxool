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
                               (j/call scene :clone))
                               
        ref (useRef)
        domAtom (:domAtom props)]
    (when (:shadow? props)
      (doseq [o (j/get-in copied-scene [:children])]
        (j/assoc! o :castShadow true)
        (j/assoc! o :receiveShadow true)))
    ;; (swap! domAtom assoc (:id props) ref)
    ($ :primitive {:object copied-scene
                   :ref ref
                   :dispose nil})))


(defn GltfView [props env]
  (let [gltf @(p/pull (:conn env) '[*] (:db/id props))
        {:gltf/keys [url scale position rotation shadow?]} gltf]
    [:mesh {:scale (or scale [1 1 1])
            :position (or position [0 0 0])
            :rotation (or rotation [0 0 0])
            :castShadow true
            :receiveShadow true}
     [:> Suspense {:fallback nil}
      [:> Model {:url url
                 :id (:db/id gltf)
                 :domAtom (:dom-atom env)
                 :shadow? shadow?}]]]))