(ns astronomy.model.coordinate
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.three.matrix4 :as m4]
   [astronomy.model.star :as m.star]
   [astronomy.model.planet :as m.planet]
   [astronomy.model.satellite :as m.satellite]))


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


(defn pull-ref-fully [db id]
  (d/pull db '[* {:coordinate/track-position [*]
                  :coordinate/track-rotation [*]}] id))


(defn cal-world-position [db id]
  (let [ref (d/pull db '[* {:coordinate/track-position [:db/id :entity/type]}] id)
        p-object (d/pull db '[*] (-> ref :coordinate/track-position :db/id))
        world-position (case (:entity/type p-object)
                         :star (:object/position p-object)
                         :planet (m.planet/cal-world-position db (:db/id p-object))
                         :satellite (m.satellite/cal-world-position db (:db/id p-object)))]
    world-position))

(defn cal-world-quaternion [db id]
  (let [ref (d/pull db '[* {:coordinate/track-rotation [:db/id :entity/type]}] id)
        r-object (d/pull db '[*] (-> ref :coordinate/track-rotation :db/id))
        world-quaternion (:object/quaternion r-object)]
    world-quaternion))


(defn cal-invert-matrix [ref]
  (let [{:coordinate/keys [position quaternion]} ref
        mat (m4/compose (v3/from-seq position) (q/from-seq quaternion) (v3/vector3 1 1 1))]
    (m4/invert mat)))


(defn sub-coordinate [conn id]
  @(p/pull conn '[* {:coordinate/track-position [*]
                     :coordinate/track-rotation [*]}] id))




