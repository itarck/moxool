(ns laboratory.parts.object.view
  (:require
   [fancoil.base :as base]
   [applied-science.js-interop :as j]
   [reagent.dom]
   ["three" :as three]
   ["@react-three/drei" :refer [Box Plane OrbitControls]]))


(defmethod base/view :object/view
  [{:keys [subscribe]} _signal {:keys [object]}]
  ^{:key (:db/id object)}
  [:> Box {:on-click (fn [e]
                       (let [inter (j/get-in e [:intersections 0 :point])]
                         (js/console.log "box click" inter)))
           :args [1 1 1]
           :position (:object/position object)
           :rotation (:object/rotation object)
           :scale (:object/scale object)}
   [:meshStandardMaterial {:color "red"
                           :side three/DoubleSide
                           :opacity 0.1
                           :transparent true}]])

