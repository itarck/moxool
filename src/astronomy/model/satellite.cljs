(ns astronomy.model.satellite
  (:require
   [cljs.spec.alpha :as s]
   [posh.reagent :as p]
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


;; selector

(def wide-selector '[* {:satellite/planet [* {:planet/star [*]}]
                        :celestial/clock [*]}])


(defn cal-world-position [db satellite]
  (let [planet (d/pull db '[*] (-> satellite :satellite/planet :db/id))
        star (d/pull db '[*] (-> planet :planet/star :db/id))]
    (mapv + (:object/position satellite)
          (:object/position planet)
          (:object/position star))))


;; subs

(defn sub-world-position [conn satellite-id]
  (let [satellite @(p/pull conn '[:object/position :satellite/planet] satellite-id)
        planet @(p/pull conn '[:object/position :planet/star] (-> satellite :satellite/planet :db/id))
        star @(p/pull conn '[:object/position] (-> planet :planet/star :db/id))]
    (mapv + (:object/position satellite)
          (:object/position planet)
          (:object/position star))))


(defn sub-planet [conn satellite]
  {:pre [(s/valid? (s/keys :req [:db/id]) satellite)]}
  (let [satellite-1 @(p/pull conn '[{:satellite/planet [*]}] (:db/id satellite))]
    (:satellite/planet satellite-1)))


(comment)