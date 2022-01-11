(ns astronomy2.plugin.astro-scene
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]
   [posh.reagent :as p]
   [reagent.core :as r]
   [fancoil.base :as base]
   [fancoil.module.posh.base :as posh.base]))

;; data

(def sample
  #:astro-scene {:astro-scene/clock [:clock/name "default"]
                 :astro-scene/camera [:camera/name "default"]
                 :scene/name "astronomy"
                 :scene/chinese-name "天文学场景"
                 :object/_scene [{:db/id 10} {:db/id 30}]})

;; schema

(defmethod posh.base/schema :astro-scene/schema
  [_ _]
  #:astro-scene{:coordinate {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
                :clock {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
                :camera {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

;; spec

(defmethod base/spec :astro-scene/entity
  [_ _]
  (s/def :astronomy/entity
    (s/keys :req [:astro-scene/coordinate :astro-scene/clock :astro-scene/camera])))


;; model

(defmethod base/model :astro-scene/is-scene-coordinate?
  [{:keys [spec]} _ {:keys [scene coordinate]}]
  (spec :assert (s/keys :req [:astro-scene/coordinate]) scene)
  (= (get-in scene [:astro-scene/coordinate :db/id])
     (:db/id coordinate)))

(defmethod base/model :astro-scene/set-scene-coordinate-tx
  [_ _ {:keys [astro-scene coordinate]}]
  [{:db/id (:db/id astro-scene)
    :astro-scene/coordinate (:db/id coordinate)}])

(defmethod base/model :astro-scene/refresh-tx
  [core _ {:keys [db astro-scene]}]
  (let [clock-id (get-in astro-scene [:astro-scene/clock :db/id])
        celes (base/model core :celestial/find-all-by-clock {:db db :clock-id clock-id})
        tx1 (mapcat #(base/model core :celestial/update-current-position-and-quaternion-tx %) celes)
        db3 (d/db-with db tx1)
        coor-ids (base/model core :coordinate/find-all-ids db3)
        tx3 (mapcat (fn [id]
                      (let [coor (d/pull db3 '[:db/id :entity/type] id)]
                        (base/model core :coordinate/update-position-and-quaternion-tx
                                    {:db db3 :coordinate coor})))
                    coor-ids)]
    (concat tx1 tx3)))

(defmethod base/model :astro-scene/put-objects-tx
  [_ _ {:keys [astro-scene objects]}]
  (mapv (fn [obj] {:db/id (:db/id obj)
                   :object/scene (:db/id astro-scene)})
        objects))

(defmethod base/model :astro-scene/remove-objects-tx
  [_ _ {:keys [astro-scene objects]}]
  (mapv (fn [obj]
          [:db/retract (:db/id obj) :object/scene (:db/id astro-scene)])
        objects))


;; subscribe

(defmethod base/subscribe :astro-scene/sub-scene-with-objects
  [{:keys [pconn]} _ {:keys [id]}]
  (p/pull pconn '[* {:object/_scene [*]}] id))

(defmethod base/subscribe :astro-scene/sub-scene-name-exist?
  [{:keys [pconn]} _ {:keys [scene-name]}]
  (r/reaction
   (boolean (seq @(p/q '[:find [?id ...]
                         :in $ ?scene-name
                         :where
                         [?id :scene/name ?scene-name]]
                       pconn scene-name)))))

(defmethod base/subscribe :astro-scene/sub-scene-coordinate
  [{:keys [pconn]} _ {:keys [id]}]
  (let [scene1 @(p/pull pconn '[*] id)]
    (p/pull pconn '[*] (get-in scene1 [:astro-scene/coordinate :db/id]))))

(defmethod base/subscribe :astro-scene/has-day-light?
  [{:keys [pconn]} _ {:keys [astro-scene]}]
  (let [atmosphere @(base/subscribe :atmosphere/sub-unique-one {})]
    (base/subscribe :atmosphere/sub-has-day-light? atmosphere)))


(defmethod base/subscribe :astro-scene/sub-scene-center-entity
  [{:keys [pconn]} _ {:keys [astro-scene]}]
  (let [scene-1 @(p/pull pconn '[{:astro-scene/coordinate [:coordinate/type
                                                           :astronomical-coordinate/center-object
                                                           :terrestrial-coordinate/center-object
                                                           :horizon-coordinate/center-object]}]
                         (:db/id astro-scene))
        coordinate-1 (:astro-scene/coordinate scene-1)
        center-entity (case (get-in coordinate-1 [:coordinate/type])
                        :horizon-coordinate (get-in coordinate-1 [:horizon-coordinate/center-object])
                        :terrestrial-coordinate (get-in coordinate-1 [:terrestrial-coordinate/center-object])
                        :astronomical-coordinate (get-in coordinate-1 [:astronomical-coordinate/center-object]))]

    (r/reaction center-entity)))

