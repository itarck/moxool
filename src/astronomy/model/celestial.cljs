(ns astronomy.model.celestial
  (:require
   [datascript.core :as d]
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [shu.three.matrix4 :as m4]
   [astronomy.model.circle-orbit :as m.circle-orbit]
   [astronomy.model.spin :as m.spin]))


;; abstract model

(def celestial-1
  #:celestial
   {:orbit #:circle-orbit {:start-position [0 0 -500]
                           :axis [0 1 0]
                           :angular-velocity (/ Math/PI 180)}
    :spin #:spin {:axis [0 1 0]
                  :angular-velocity (* 2 Math/PI)}
    :clock [:clock/name "default"]})


(def schema
  #:celestial {:orbit {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one :db/isComponent true}
               :spin {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one :db/isComponent true}
               :clock {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
               :gltf {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


(defn cal-position [celestial days]
  (let [{:celestial/keys [orbit]} celestial]
    (if orbit
      (m.circle-orbit/cal-position orbit days)
      [0 0 0])))

(defn cal-position-matrix [celestial days]
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

;; find

(defn find-celestials-by-clock [db clock]
  (let [cele-ids (d/q '[:find [?id ...]
                        :in $ ?clock-id
                        :where [?id :celestial/clock ?clock-id]]
                      db (:db/id clock))
        celes (d/pull-many db '[*] cele-ids)]
    celes))

;; create transact

(defn update-position-tx [celestial clock]
  (if (:celestial/orbit celestial)
    (let [{id :db/id :celestial/keys [orbit]} celestial
          {:clock/keys [time-in-days]} clock
          position (vec (m.circle-orbit/cal-position orbit time-in-days))]
      [[:db/add id :object/position position]])
    []))

(defn update-quaternion-tx [celestial clock]
  (if (:celestial/spin celestial)
    (let [{id :db/id :celestial/keys [spin]} celestial
          {:clock/keys [time-in-days]} clock
          quaternion (vec (m.spin/cal-quaternion spin time-in-days))]
      [[:db/add id :object/quaternion quaternion]])
    []))

(defn update-matrix-tx [celestial clock]
  (let [matrix (cal-matrix celestial clock)]
    [[:db/add (:db/id celestial) :celestial/matrix (:row-seq matrix)]]))

(defn update-position-and-quaternion-tx [celestial clock]
  (vec (concat (update-position-tx celestial clock)
               (update-quaternion-tx celestial clock))))


