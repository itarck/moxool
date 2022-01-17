(ns laboratory.plugin.scene
  (:require
   [cljs.spec.alpha :as s]
   [posh.reagent :as p]
   [laboratory.base :as base]
   ["@react-three/drei" :refer [OrbitControls]]
   ["react-three-fiber" :refer [Canvas]]))

;; value

(def sample 
  #:scene{:name "default"
          :background "white"
          :framework/_scene [:framework/name "default"]})

;; schema

(defmethod base/schema :scene/schema
  [_ _]
  {:scene/name {:db/unique :db.unique/identity}})

;; spec

(defmethod base/spec :scene/spec
  [_ _]
  (base/spec {} :entity/spec)
  (s/def :scene/name string?)
  (s/def :scene/scene (s/keys :req [:db/id :scene/name])))

;; model

(defmethod base/model :scene/create
  [_ _ entity]
  (let [default #:scene {:name "default"
                         :background "white"
                         :framework/_scene [:framework/name "default"]}]
    (merge default entity)))


;; sub

(defmethod base/subscribe :scene/pull
  [{:keys [pconn]} _ {:keys [entity pattern]}]
  (let [pattern (or pattern '[* {:object/_scene [*]}])]
    (p/pull pconn pattern (:db/id entity))))

;; view

(defmethod base/view :scene/view
  [{:keys [subscribe] :as core} _signal scene]
  (let [scene @(subscribe :scene/pull {:entity scene})
        objects (:object/_scene scene)]
    [:> Canvas {:camera {:position [1 3 3]}
                :style {:background (:scene/background scene)}}
     [:ambientLight {:intensity 0.5}]
     [:> OrbitControls]
     [:gridHelper {:args [100 100] :position [0 0 0]}]
     (for [object objects]
       ^{:key (:db/id object)}
       [base/view core :object/view object])]))

