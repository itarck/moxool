(ns architecture.plugin.cylinder
  (:require
   [fancoil.base :as base]
   [applied-science.js-interop :as j]
   ["three" :as three]
   ["@react-three/drei" :refer [Cylinder]]))


;; schema


;; model 

(defmethod base/model :cylinder/create
  [_ _ props]
  (let [default #:object {:type :cylinder
                          :position [0 0 0]
                          :rotation [0 0 0]
                          :scale [1 1 1]
                          :scene [:scene/name "default"]}]
    (merge default props)))

;; view 

(defmethod base/view :cylinder/view
  [_core _method object]
  ^{:key (:db/id object)}
  [:> Cylinder {:on-click (fn [e]
                            (let [inter (j/get-in e [:intersections 0 :point])]
                              (js/console.log "box click" inter)))
                :args [1 1 3 20]
                :position (:object/position object)
                :rotation (:object/rotation object)
                :scale (:object/scale object)}
   [:meshStandardMaterial {:color "green"
                           :side three/DoubleSide
                           :opacity 0.5
                           :transparent true}]])

