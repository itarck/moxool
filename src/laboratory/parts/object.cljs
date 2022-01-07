(ns laboratory.parts.object
  (:require
   [fancoil.base :as base]
   [fancoil.module.posh.base :as posh.base]
   [applied-science.js-interop :as j]
   [reagent.dom]
   ["three" :as three]
   ["@react-three/drei" :refer [Box]]))


(defmethod posh.base/schema :object/schema
  [_ _]
  {:object/name {:db/unique :db.unique/identity}
   :object/scene {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})



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

