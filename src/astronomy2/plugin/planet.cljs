(ns astronomy2.plugin.planet
  (:require 
   [cljs.spec.alpha :as s]
   [laboratory.base :as base]))


;; data

(def planet-1
  #:planet
   {:db/id -1
    :name "earth"
    :chinese-name "地球"
    :radius 5
    :color "blue"
    :star {:db/id [:star/name "sun"]}
    :gltf/url "models/11-tierra/scene.gltf"
    :gltf/scale [0.2 0.2 0.2]

    :object/scene {:db/id [:scene/name "solar"]}
    :object/position [0 0 100]
    :object/quaternion [0 0 0 1]
    :entity/type :planet})

;; schema

(defmethod base/schema :planet/schema
  [_ _]
  {:planet/name {:db/unique :db.unique/identity}
   :planet/star {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

;; spec

(defmethod base/spec :planet/spec
  [_ _]
  (base/spec {} :entity/spec)
  (s/def :planet/name string?)
  (s/def :planet/star :entity/entity)
  (s/def :planet/planet (s/keys :req [:db/id :planet/star]
                                :opt [:planet/name])))

;; model

(defmethod base/model :planet/create
  [_ _ props]
  (let [default #:planet
                 {:object/type :planet}]
    (merge default props)))

;; subscribe 

(defmethod base/subscribe :planet/pull
  [core _ {:keys [entity]}]
  (base/subscribe core :pull '[*] (:db/id entity)))

(defmethod base/subscribe :planet/sub-planets-with-chinese-names
  [core _ _]
  (let [query '[:find ?id ?chinese-name
                :where
                [?id :object/type :planet]
                [?id :planet/chinese-name ?chinese-name]]]
    (base/subscribe core :q query)))

;; view 

(defmethod base/view :planet/view
  [{:keys [subscribe] :as core} _ entity]
  (let [planet @(subscribe :entity/pull {:entity entity})]
    (when (:gltf/url planet)
      [base/view core :gltf/view planet])))
