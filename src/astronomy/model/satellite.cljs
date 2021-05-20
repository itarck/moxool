(ns astronomy.model.satellite
  (:require
   [datascript.core :as d]))

;; 包含ns: planet


;; model

(def schema {:satellite/name {:db/unique :db.unique/identity}
             :satellite/planet {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

;; data 

(def satellite-1
  #:satellite
   {:name "moon"
    :chinese-name "月球"
    :radius 2
    :color "green"
    :planet [:planet/name "earth"]

    :celestial/orbit #:circle-orbit {:start-position [0 0 30]
                                     :axis [-1 1 0]
                                     :angular-velocity (/ (* Math/PI 2) 30)}
    :celestial/spin #:spin {:axis [-1 1 0]
                            :angular-velocity (/ (* Math/PI 2) 30)}
    :celestial/gltf #:gltf {:url "models/11-tierra/scene.gltf"
                            :scale [0.2 0.2 0.2]}
    :celestial/clock [:clock/name "default"]

    :object/scene [:scene/name "solar"]
    :object/position [0 0 30]
    :object/quaternion [0 0 0 1]
    :entity/type :satellite})




;; computed view



(comment)