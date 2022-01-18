(ns astronomy2.plugin.star
  (:require
   [cljs.spec.alpha :as s]
   [laboratory.base :as base]
   [applied-science.js-interop :as j]
   [helix.core :refer [defnc $]]
   [posh.reagent :as p]
   ["@react-three/drei" :refer [Sphere]]
   ["three" :as three]))


;; data 

(def sample
  {:star/name "sun"
   :star/chinese-name "太阳"
   :star/show-light? true

   :celestial/radius 2.321606103
   :celestial/radius-string "109.1 地球半径"

   :gltf/url "models/16-solar/Sun_1_1391000.glb"
   :gltf/scale [0.002 0.002 0.002]
   :gltf/shadow? false

   :object/position [0 0 0]
   :object/quaternion [0 0 0 1]
   :object/show? true
   :object/type :star})


;; schema 

(defmethod base/schema :star/schema
  [_ _]
  #:star{:name {:db/unique :db.unique/identity}})


;; spec

(defmethod base/spec :star/spec
  [_ _]
  (base/spec {} :entity/spec)
  (s/def :star/name string?)
  (s/def :planet/_star (s/coll-of :entity/entity))
  (s/def :star/star (s/keys :req [:db/id]
                            :opt [:star/name])))

;; model

(defmethod base/model :star/create
  [_ _ props]
  (let [default {:object/type :star}]
    (merge default props)))

;; sub

(defmethod base/subscribe :star/pull
  [{:keys [pconn]} _ {:keys [entity]}]
  (s/assert :entity/entity entity)
  (p/pull pconn '[* {:planet/_star [:db/id]}] (:db/id entity)))

;; view 

(defnc DirectionalSunLight [props]
  (let [{:keys [position intensity shadow-camera-near shadow-camera-far shadow-camera-size]} props
        l (new three/DirectionalLight "white" intensity)]
    (j/apply-in l [:position :set] position)
    (j/assoc! l :castShadow true)
    ;; (j/assoc! l :decay 2)
    (j/assoc-in! l [:shadow :camera :near] shadow-camera-near)
    (j/assoc-in! l [:shadow :camera :far] shadow-camera-far)
    (j/assoc-in! l [:shadow :camera :left] (- shadow-camera-size))
    (j/assoc-in! l [:shadow :camera :right] shadow-camera-size)
    (j/assoc-in! l [:shadow :camera :bottom] (- shadow-camera-size))
    (j/assoc-in! l [:shadow :camera :top] shadow-camera-size)
    ($ :primitive {:object l})))


(defmethod base/view :star/view
  [{:keys [subscribe] :as core} _ entity]
  (let [star @(subscribe :entity/pull {:entity entity})
        {:star/keys [color]} star
        {:object/keys [position quaternion]} star]
    [:mesh {:position (or position [0 0 0])}

     (if (:gltf/url star)
       [:mesh {:quaternion (or quaternion [0 0 0 1])}
        [base/view core :gltf/view star]]
       [:> Sphere {:args [(:celestial/radius star) 10 10]
                   :position [0 0 0]
                   :quaternion (or quaternion [0 0 0 1])}
        [:meshStandardMaterial {:color color}]])

     #_[:<>
      (for [planet planets]
        ^{:key (:db/id planet)}
        [planet.v/PlanetView {:planet planet} ])]])
  )

