(ns architecture.plugin.box
  (:require
   [fancoil.base :as base]
   [applied-science.js-interop :as j]
   ["three" :as three]
   ["@react-three/drei" :refer [Box]]))


;; schema


;; model 

(defmethod base/model :box/create
  [_ _ props]
  (let [default #:object {:type :box
                          :position [0 0 0]
                          :rotation [0 0 0]
                          :scale [1 1 1]
                          :scene [:scene/name "default"]}]
    (merge default props)))

;; view 

(defmethod base/view :box/view
  [_core _method object]
  ^{:key (:db/id object)}
  [:> Box {:on-click (fn [e]
                       (let [inter (j/get-in e [:intersections 0 :point])]
                         (js/console.log "box click" inter)))
           :args [1 1 1]
           :position (:object/position object)
           :rotation (:object/rotation object)
           :scale (:object/scale object)}
   [:meshStandardMaterial {:color "blue"
                           :side three/DoubleSide
                           :opacity 0.5
                           :transparent true}]])

