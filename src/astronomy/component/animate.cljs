(ns astronomy.component.animate
  (:require
   [applied-science.js-interop :as j]
   [helix.core :refer [defnc $] :as h]
   [cljs-bean.core :refer [bean ->clj ->js]]
   [posh.reagent :as p]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.three.matrix4 :as m4]
   ["react" :as react :refer [useRef]]
   ["react-three-fiber" :refer [Canvas useFrame extend useThree]]))


(defnc AnimatedMeshComponent [{:keys [use-frame-fn position quaternion scale children] :as props}]
  (let [mesh-ref (useRef)]
    (useFrame #(use-frame-fn mesh-ref))
    ($ "mesh" {:ref mesh-ref
               :position (or position #js [0 0 0])
               :quaternion (or quaternion #js [0 0 0 1])
               :scale (or scale #js [1 1 1])}
       children)))


(defn cal-matrix [obj]
  (let [{:object/keys [position quaternion]} obj
        mat (m4/compose (v3/from-seq position) (q/from-seq quaternion) (v3/vector3 1 1 1))]
    mat))

(defn cal-invert-matrix [obj]
  (m4/invert (cal-matrix obj)))


(defn update-coordinate-mesh [conn id mesh]
  (let [object @(p/pull conn '[:object/quaternion :object/position] id)
        im (cal-invert-matrix object)]
    (doto mesh
      (j/assoc-in! [:current :matrix] im))))

(defnc AnimatedCoordinateComponent [{:keys [use-frame-fn children] :as props}]
  (let [mesh-ref (useRef)]
    (useFrame #(use-frame-fn mesh-ref))
    ($ "mesh" {:ref mesh-ref
               :matrixAutoUpdate false}
       children)))


