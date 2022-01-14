(ns laboratory.plugin.scene
  (:require
   [posh.reagent :as p]
   [laboratory.base :as base]
   ["@react-three/drei" :refer [OrbitControls]]
   ["react-three-fiber" :refer [Canvas]]))


(defmethod base/schema :scene/schema
  [_ _]
  {:scene/name {:db/unique :db.unique/identity}})

;; model

(defmethod base/model :scene/create
  [_ _ props]
  (let [default #:scene {:name "default"
                         :background "white"
                         :framework/_scene [:framework/name "default"]}]
    (merge default props)))


;; sub

(defmethod base/subscribe :scene/pull-one
  [{:keys [pconn]} _ {:db/keys [id]}]
  (p/pull pconn '[* {:object/_scene [*]}] id))

;; view

(defmethod base/view :scene/view
  [{:keys [subscribe] :as core} _signal props]
  (let [scene @(subscribe :scene/pull-one props)
        objects (:object/_scene scene)]
    [:> Canvas {:camera {:position [1 3 3]}
                :style {:background (:scene/background scene)}}
     [:ambientLight {:intensity 0.5}]
     [:> OrbitControls]
     [:gridHelper {:args [100 100] :position [0 0 0]}]
     (for [object objects]
       ^{:key (:db/id object)}
       [base/view core :object/view object])]))

