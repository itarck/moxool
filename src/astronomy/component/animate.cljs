(ns astronomy.component.animate
  (:require
   [applied-science.js-interop :as j]
   [helix.core :refer [defnc $] :as h]
   [cljs-bean.core :refer [bean ->clj ->js]]
   ["react" :as react :refer [useRef]]
   ["react-three-fiber" :refer [Canvas useFrame extend useThree]]))


(defnc AnimatedMeshComponent [{:keys [use-frame-fn position rotation scale children] :as props}]
  (let [mesh-ref (useRef)]
    (useFrame #(use-frame-fn mesh-ref))
    ($ "mesh" {:ref mesh-ref
               :position (or position #js [0 0 0])
               :rotation (or rotation #js [0 0 0 1])
               :scale (or scale #js [1 1 1])}
       children)))
