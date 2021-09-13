(ns astronomy.objects.satellite.m
  (:require
   [cljs.spec.alpha :as s]
   [posh.reagent :as p]
   [datascript.core :as d]
   [astronomy.objects.celestial.m :as m.celestial]
   [astronomy.objects.planet.m :as planet.m]))

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

(def query-all-id-and-chinese-name
  '[:find ?id ?chinese-name
    :where
    [?id :entity/type :satellite]
    [?id :entity/chinese-name ?chinese-name]])


;; transform

(defn cal-system-position-now [db satellite]
  (let [planet (d/pull db '[*] (-> satellite :satellite/planet :db/id))
        star (d/pull db '[*] (-> planet :planet/star :db/id))]
    (mapv + (:object/position satellite)
          (:object/position planet)
          (:object/position star))))

(defn cal-local-position-at-epoch
  "local position 是对应父参考系的"
  [db satellite epoch-days]
  (let [satellite-1 (d/pull db '[* {:celestial/orbit [*]}] (:db/id satellite))
        object-position (m.celestial/cal-position satellite-1 epoch-days)]
    object-position))

(defn cal-system-position-at-epoch
  "system positon 是对应在系统参考系，也就是 J2000历元的赤道天球"
  [db satellite epoch-days]
  (let [satellite-1 (d/pull db '[* {:celestial/orbit [*]}] (:db/id satellite))
        object-position (m.celestial/cal-position satellite-1 epoch-days)
        planet-position (planet.m/cal-system-position-at-epoch db (:satellite/planet satellite-1) epoch-days)]
    (mapv + object-position planet-position)))


;; abstract

(defmethod m.celestial/cal-system-position-now :satellite
  [db satellite]
  (cal-system-position-now db satellite))


(defmethod m.celestial/cal-system-position-at-epoch :satellite
  [db satellite epoch-days]
  (cal-system-position-at-epoch db satellite epoch-days))


;; subs


(defn sub-planet [conn satellite]
  {:pre [(s/valid? (s/keys :req [:db/id]) satellite)]}
  (let [satellite-1 @(p/pull conn '[{:satellite/planet [*]}] (:db/id satellite))]
    (:satellite/planet satellite-1)))


(comment)