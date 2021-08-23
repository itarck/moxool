(ns astronomy.objects.planet.m
  (:require
   [datascript.core :as d]
   [shu.three.vector3 :as v3]
   [shu.three.spherical :as sph]
   [shu.three.quaternion :as q]
   [posh.reagent :as p]
   [astronomy.model.coordinate :as m.coordinate]
   [astronomy.model.celestial :as m.celestial]))

;; 包含ns: planet


;; model

(def schema {:planet/name {:db/unique :db.unique/identity}
             :planet/star {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

;; data 

(def planet-1
  #:planet
   {:name "earth"
    :chinese-name "地球"
    :radius 5
    :color "blue"
    :star [:star/name "sun"]

    :celestial/orbit #:circle-orbit {:star [:star/name "sun"]
                                     :start-position [0 0 100]
                                     :axis [-1 1 0]
                                     :angular-velocity (/ Math/PI 180)}
    :celestial/spin #:spin {:axis [0 1 0]
                            :angular-velocity (* Math/PI 2)}
    :celestial/gltf #:gltf {:url "models/11-tierra/scene.gltf"
                            :scale [0.2 0.2 0.2]}
    :celestial/clock [:clock/name "default"]

    :object/scene [:scene/name "solar"]
    :object/position [0 0 100]
    :object/quaternion [0 0 0 1]
    :entity/type :planet})

;; query

(def query-all-ids
  '[:find [?id ...]
    :where [?id :entity/type :planet]])

(def query-all-ids-with-tracker
  '[:find [?id ...]
    :where
    [?id :entity/type :planet]
    [?id :planet/track-position? true]])

(def query-all-id-and-chinese-name
  '[:find ?id ?chinese-name
    :where
    [?id :entity/type :planet]
    [?id :entity/chinese-name ?chinese-name]])

;; model 

(defn is-coordinate-center? 
  "代码旧了，要改动"
  [planet coordinate]
  (= (:db/id planet) (get-in coordinate [:coordinate/track-position :db/id])))

(defn random-position [radius axis]
  (let [v1 (v3/from-spherical (sph/spherical radius (/ Math/PI 2) (* Math/PI 2 (rand))))
        q1 (q/from-unit-vectors (v3/vector3 0 1 0) (v3/from-seq axis))]
    (seq (v3/apply-quaternion v1 q1))))

(defn cal-current-system-position
  "在系统参考系里的位置"
  [db planet]
  (let [planet-1 (d/pull db '[:object/position {:planet/star [:object/position]}] (:db/id planet))]
    (mapv + (:object/position planet-1)
          (get-in planet-1 [:planet/star :object/position]))))

(defn cal-system-position
  "在系统参考系里的位置，如果带时间，就计算指定时间"
  [db planet epoch-days]
  (let [planet-1 (d/pull db '[* {:celestial/orbit [*]
                                 :planet/star [:object/position]}] (:db/id planet))
        object-position (m.celestial/cal-position planet-1 epoch-days)
        star-position (get-in planet-1 [:planet/star :object/position])]
    (mapv + object-position star-position)))

(defn cal-position-in-coordinate
  [db planet coordinate]
  (let [world-position (cal-current-system-position db planet)
        local-position (m.coordinate/from-system-vector coordinate world-position)]
    local-position))


#_(defn cal-coordinate-position
  "在指定参考系内的位置，给定时间"
  [db planet coordinate epoch-days]
  (let [system-position (cal-system-position db planet epoch-days)
        im (ac.m/cal-invert-matrix db coordinate epoch-days)]
    (vec (v3/apply-matrix4 system-position im))))



;; sub



;; tx

(defn update-all-local-position 
  "在当前系统坐标系下的位置"
  [db coordinate]
  (let [ids (d/q query-all-ids db)]
    (mapv (fn [id] {:db/id id
                    :planet/position (cal-position-in-coordinate db {:db/id id} coordinate)})
          ids)))

(defn update-all-position-logs [db]
  (let [ids (d/q query-all-ids-with-tracker db)]
    (mapv (fn [id]
            (let [p (d/pull db '[*] id)]
              {:db/id id
               :planet/position-log (conj (:planet/position-log p) (:planet/position p))}))
          ids)))