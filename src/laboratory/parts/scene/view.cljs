(ns laboratory.parts.scene.view
  (:require
   [fancoil.base :as base]
   [applied-science.js-interop :as j]
   [helix.core :refer [defnc $] :as h]
   [reagent.dom]
   ["three" :as three]
   ["camera-controls" :as CameraControls]
   ["@react-three/drei" :refer [Box Plane OrbitControls]]
   ["react-three-fiber" :refer [Canvas useFrame extend useThree]]))


(extend #js {:CameraControls CameraControls})


(defmethod base/view :scene/view
  [{:keys [subscribe] :as config} _signal {:keys [scene]}]
  (let [scene @(subscribe :scene/pull-one scene)
        objects (:object/_scene scene)]
    (println scene)
    [:> Canvas {:camera {:position [1 3 3]}
                :style {:background (:scene/background scene)}}
     [:ambientLight {:intensity 0.5}]
     [:> OrbitControls]
     [:gridHelper {:args [100 100] :position [0 0 0]}]
     (for [object objects]
       (base/view config :object/view {:object object}))]))

