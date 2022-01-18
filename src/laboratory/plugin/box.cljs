(ns laboratory.plugin.box
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
  [_core _method box]
  ^{:key (:db/id box)}
  (let [{:object/keys [position rotation scale]
         :box/keys [args]
         :or {position [0 0 0]
              rotation [0 0 0]
              scale [1 1 1]
              args [1 1 1]}} box]
    [:> Box {:on-click (fn [e]
                         (let [inter (j/get-in e [:intersections 0 :point])]
                           (js/console.log "box click" inter)))
             :args args
             :position position
             :rotation rotation
             :scale scale}
     [:meshStandardMaterial {:color (or (:box/color box) "blue")
                             :side three/DoubleSide
                             :opacity 0.5
                             :transparent true}]])
  )

