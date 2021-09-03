(ns astronomy.model.celestial
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]
   [shu.three.quaternion :as q]
   [shu.three.matrix4 :as m4]
   [astronomy.objects.ellipse-orbit.m :as ellipse-orbit.m]
   [astronomy.objects.circle-orbit.m :as circle-orbit.m]
   [astronomy.objects.moon-orbit.m :as moon-orbit.m]
   [astronomy.model.spin :as m.spin]))


;; schema

(def schema
  #:celestial {:orbit {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one :db/isComponent true}
               :spin {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one :db/isComponent true}
               :clock {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
               :gltf {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
               :group {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

(s/def :astronomy/celestial
  (s/keys :req [:db/id :celestial/orbit :celestial/spin :celestial/clock]
          :opt [:celestial/gltf :celestial/name]))


(comment

  (def celestial-1
    #:celestial
     {:orbit #:circle-orbit {:start-position [0 0 -500]
                             :axis [0 1 0]
                             :angular-velocity (/ Math/PI 180)}
      :spin #:spin {:axis [0 1 0]
                    :angular-velocity (* 2 Math/PI)}
      :clock [:clock/name "default"]
      :name "地球"
      :db/id -1})

  (s/valid? :astronomy/celestial celestial-1)

  ;; 
  )


;; find

(def query-ids-by-clock
  '[:find [?id ...]
    :in $ ?clock-id
    :where [?id :celestial/clock ?clock-id]])

(def query-all-id-and-chinese-name
  '[:find ?id ?chinese-name
    :where
    [?id :celestial/gltf _]
    [?id :celestial/clock]
    [?id :entity/chinese-name ?chinese-name]])

(def select-with-clock
  '[* {:celestial/clock [*]}])

(defn find-all-by-clock [db clock-id]
  (let [ids (d/q query-ids-by-clock db clock-id)
        celes (d/pull-many db select-with-clock ids)]
    celes))

(defn pull-whole [db id]
  (d/pull db select-with-clock id))


;; models

(defn cal-position [celestial days]
  {:pre [(s/assert :astronomy/celestial celestial)]}
  (let [{:celestial/keys [orbit]} celestial]
    (cond
      (and orbit (= (:orbit/type orbit) :ellipse-orbit)) (ellipse-orbit.m/cal-position orbit days)
      (and orbit (= (:orbit/type orbit) :moon-orbit)) (seq (moon-orbit.m/cal-position-vector orbit days))
      orbit (circle-orbit.m/cal-position orbit days)
      :else (:object/position celestial))))

(defn cal-position-matrix [celestial days]
  {:pre [(s/assert :astronomy/celestial celestial)]}
  (let [position (cal-position celestial days)]
    (apply m4/make-translation position)))

(defn cal-quaternion [celestial days]
  (let [{:celestial/keys [spin]} celestial]
    (if spin
      (m.spin/cal-quaternion spin days)
      (:object/quaternion celestial))))

(defn cal-tilt-matrix [celestial days]
  (let [{:celestial/keys [spin]} celestial]
    (if spin
      (m.spin/cal-tilt-matrix spin days)
      (m4/identity-matrix4))))

(defn cal-self-spin-matrix [celestial days]
  (let [{:celestial/keys [spin]} celestial]
    (if spin
      (m.spin/cal-self-spin-matrix spin days)
      (m4/identity-matrix4))))

(defn cal-spin-matrix [celestial days]
  (let [{:celestial/keys [spin]} celestial]
    (if spin
      (m4/make-rotation-from-quaternion (m.spin/cal-quaternion spin days))
      (m4/identity-matrix4))))

(defn cal-matrix [celestial days]
  (let [orbit-matrix (cal-position-matrix celestial days)
        spin-matrix (cal-spin-matrix celestial days)]
    (m4/multiply orbit-matrix spin-matrix)))

;; interface

(defmulti cal-system-position-now
  "当前星体在系统内的位置"
  (fn [_db celes] (:entity/type celes)))

(defmulti cal-system-position-at-epoch
  "指定 epoch-days， 星体在系统内的位置"
  (fn [_db celestial _epoch-days] (:entity/type celestial)))


;; create transact

(defn update-current-matrix-tx [celestial]
  (let [{:celestial/keys [clock]} celestial
        {:clock/keys [time-in-days]} clock
        matrix (cal-matrix celestial time-in-days)]
    [[:db/add (:db/id celestial) :celestial/matrix (:row-seq matrix)]]))

(defn update-current-position-and-quaternion-tx [celestial]
  {:pre [(s/assert :astronomy/celestial celestial)
         (s/assert :astronomy/clock (:celestial/clock celestial))]}
  (let [{id :db/id :celestial/keys [clock]} celestial
        {:clock/keys [time-in-days]} clock
        position (cal-position celestial time-in-days)
        quaternion (cal-quaternion celestial time-in-days)]
    (concat (if position [[:db/add id :object/position (seq position)]] [])
            (if quaternion [[:db/add id :object/quaternion (seq quaternion)]] []))))


(defn update-show-orbit-tx [celestial show?]
  [{:db/id (get-in celestial [:celestial/orbit :db/id])
    :orbit/show? show?}])


(defn update-show-spin-helper-tx [celestial show?]
  [{:db/id (get-in celestial [:celestial/spin :db/id])
    :spin/show-helper? show?}])

