(ns astronomy.model.coordinate
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.three.matrix4 :as m4]))


(def ref-1
  #:coordinate {:name "default"
                :clock [:clock/name "default"]
                :track-position [:planet/name "earth"]
                :track-rotation [:planet/name "earth"]})


(def schema {:coordinate/name {:db/unique :db.unique/identity}
             :coordinate/clock {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
             :coordinate/track-position {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
             :coordinate/track-rotation {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})



;; model


(defn find-ids-by-clock [db clock-id]
  (d/q '[:find [?id ...]
         :in $ ?clock-id
         :where [?id :coordinate/clock ?clock-id]]
       db clock-id))


(defn pull-coordinate-fully [db id]
  (d/pull db '[* {:coordinate/track-position [*]
                  :coordinate/track-rotation [*]}] id))

(defn sub-coordinate-fully [conn id]
  @(p/pull conn '[* {:coordinate/track-position [*]
                     :coordinate/track-rotation [*]}] id))

(defn cal-world-position [db id]
  (let [coor (d/pull db '[* {:coordinate/track-position [:db/id :entity/type]}] id)
        p-object (d/pull db '[*] (-> coor :coordinate/track-position :db/id))
        world-position (case (:entity/type p-object)
                         :star (:object/position p-object)
                         :planet (let [planet p-object
                                       star (d/pull db '[*] (-> planet :planet/star :db/id))]
                                   (mapv + (:object/position planet)
                                         (:object/position star)))
                         :satellite (let [satellite p-object
                                          planet (d/pull db '[*] (-> satellite :satellite/planet :db/id))
                                          star (d/pull db '[*] (-> planet :planet/star :db/id))]
                                      (mapv + (:object/position satellite)
                                            (:object/position planet)
                                            (:object/position star))))]
    world-position))

(defn cal-world-quaternion [db id]
  (let [coor (d/pull db '[* {:coordinate/track-rotation [:db/id :entity/type]}] id)
        r-object (d/pull db '[*] (-> coor :coordinate/track-rotation :db/id))
        world-quaternion (:object/quaternion r-object)]
    world-quaternion))


(defn cal-invert-matrix [coor]
  (let [{:coordinate/keys [position quaternion]} coor
        mat (m4/compose (v3/from-seq position) (q/from-seq quaternion) (v3/vector3 1 1 1))]
    (m4/invert mat)))

(defn original-position [coor-1]
  (v3/apply-matrix4 (v3/vector3 0 0 0) (cal-invert-matrix coor-1)))

(defn is-earth-center? [coor-1]
  (= (get-in coor-1 [:coordinate/track-position :planet/name])
     "earth"))

;; tx 

(defn update-track-position-tx [coordinate-id track-position-id]
  [{:db/id coordinate-id
    :coordinate/track-position track-position-id}])

(defn update-track-rotation-tx [coordinate-id track-rotation-id]
  [{:db/id coordinate-id
    :coordinate/track-rotation track-rotation-id}])

(defn update-coordinate-tx [db id]
  [[:db/add id :coordinate/position (cal-world-position db id)]
   [:db/add id :coordinate/quaternion (cal-world-quaternion db id)]])


;; sub

(defn sub-coordinate [conn id]
  @(p/pull conn '[* {:coordinate/track-position [*]
                     :coordinate/track-rotation [*]}] id))




