(ns laboratory.plugin.gltf
  (:require
   [cljs.spec.alpha :as s]
   [laboratory.base :as base]
   [applied-science.js-interop :as j]
   [helix.core :refer [defnc $]]
   [helix.hooks :refer [use-memo]]
   ["@react-three/drei" :refer [useGLTF]]
   ["react" :as react :refer [useRef Suspense]]))

;; data 

(def sample #:gltf {:url "models/11-tierra/scene.gltf"})

;; schema


;; spec


(defmethod base/spec :gltf/spec
  [_ _]
  (s/def :gltf/url string?))

;; model 

(defmethod base/model :gltf/create
  [_ _ entity]
  (s/assert (s/keys :req [:gltf/url]) entity)
  entity)

;; view 

(defnc Model [props]
  (let [gltf (useGLTF (:url props))
        scene (j/get gltf :scene)
        copied-scene (use-memo [scene]
                               (j/call scene :clone))

        ref (useRef)]
    (when (:shadow? props)
      (doseq [o (j/get-in copied-scene [:children])]
        (j/assoc! o :castShadow true)
        (j/assoc! o :receiveShadow true)))
    ($ :primitive {:object copied-scene
                   :ref ref
                   :dispose nil})))

(defmethod base/view :gltf/view
  [{:keys [subscribe]} _method props]
  (let [gltf @(subscribe :entity/pull {:entity props})
        {:gltf/keys [url]
         :object/keys [scale position rotation shadow?]} gltf]
    [:mesh {:scale (or scale [1 1 1])
            :position (or position [0 0 0])
            :rotation (or rotation [0 0 0])
            :castShadow true
            :receiveShadow true}
     [:> Suspense {:fallback nil}
      [:> Model {:url url
                 :id (:db/id gltf)
                 :shadow? shadow?}]]]))

