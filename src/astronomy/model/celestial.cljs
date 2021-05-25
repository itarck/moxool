(ns astronomy.model.celestial
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [shu.three.matrix4 :as m4]
   [astronomy.model.circle-orbit :as m.circle-orbit]
   [astronomy.model.spin :as m.spin]))



;; schema

(def schema
  #:celestial {:orbit {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one :db/isComponent true}
               :spin {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one :db/isComponent true}
               :clock {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
               :gltf {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

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
    (if orbit
      (m.circle-orbit/cal-position orbit days)
      [0 0 0])))

(defn cal-position-matrix [celestial days]
  {:pre [(s/assert :astronomy/celestial celestial)]}
  (let [{:celestial/keys [orbit]} celestial]
    (if orbit
      (apply m4/make-translation (m.circle-orbit/cal-position orbit days))
      (m4/identity-matrix4))))

(defn cal-quaternion [celestial days]
  (let [{:celestial/keys [spin]} celestial]
    (if spin
      (m.spin/cal-quaternion spin days)
      (q/quaternion))))

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


;; create transact

(defn update-position-tx [celestial]
  {:pre [(s/assert :astronomy/celestial celestial)
         (s/assert :astronomy/clock (:celestial/clock celestial))]}
  (if (:celestial/orbit celestial)
    (let [{id :db/id :celestial/keys [orbit]} celestial
          time-in-days (-> celestial :celestial/clock :clock/time-in-days)
          position (vec (m.circle-orbit/cal-position orbit time-in-days))]
      [[:db/add id :object/position position]])
    []))

(defn update-quaternion-tx [celestial]
  (if (:celestial/spin celestial)
    (let [{id :db/id :celestial/keys [spin clock]} celestial
          {:clock/keys [time-in-days]} clock
          quaternion (vec (m.spin/cal-quaternion spin time-in-days))]
      [[:db/add id :object/quaternion quaternion]])
    []))

(defn update-matrix-tx [celestial]
  (let [{:celestial/keys [clock]} celestial
        {:clock/keys [time-in-days]} clock
        matrix (cal-matrix celestial time-in-days)]
    [[:db/add (:db/id celestial) :celestial/matrix (:row-seq matrix)]]))

(defn update-position-and-quaternion-tx [celestial]
  {:pre [(s/assert :astronomy/celestial celestial)
         (s/assert :astronomy/clock (:celestial/clock celestial))]}
  (vec (concat (update-position-tx celestial)
               (update-quaternion-tx celestial))))


