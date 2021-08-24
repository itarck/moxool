(ns astronomy.model.astro-scene
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.model.coordinate :as m.coordinate]
   [astronomy.model.atmosphere :as m.atmosphere]))


(def sample
  #:astro-scene {:astro-scene/clock [:clock/name "default"]
                 :astro-scene/camera [:camera/name "default"]
                 :scene/name "astronomy"
                 :scene/chinese-name "天文学场景"
                 :object/_scene [{:db/id 10} {:db/id 30}]})


(def schema
  #:astro-scene{:coordinate {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
                :clock {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
                :camera {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


;; transform 

(defn is-scene-coordinate? [scene coordinate]
  (= (get-in scene [:astro-scene/coordinate :db/id])
     (:db/id coordinate)))

;; find and pull

(defn pull-one [db scene]
  (d/pull db '[*] (:db/id scene)))

(defn pull-scene-coordinate [db scene]
  (let [scene1 (pull-one db scene)]
    (d/pull db '[*] (get-in scene1 [:astro-scene/coordinate :db/id]))))

(defn pull-scene-camera [db scene]
  (let [scene1 (pull-one db scene)]
    (d/pull db '[*] (get-in scene1 [:astro-scene/camera :db/id]))))



;; sub

(defn sub-scene-with-objects [conn id]
  @(p/pull conn '[* {:object/_scene [*]}] id))


(defn sub-scene-name-exist? [conn scene-name]
  (seq
   @(p/q '[:find [?id ...]
           :in $ ?scene-name
           :where
           [?id :scene/name ?scene-name]]
         conn scene-name)))

(defn sub-scene-coordinate [conn scene]
  (let [scene1 @(p/pull conn '[*] (:db/id scene))]
    @(p/pull conn '[*] (get-in scene1 [:astro-scene/coordinate :db/id]))))

(defn sub-has-day-light? [conn scene]
  (let [atmosphere (m.atmosphere/sub-unique-one conn)]
    (m.atmosphere/sub-has-day-light? conn atmosphere)))

(defn sub-scene-center-entity [conn scene]
  (let [scene-1 @(p/pull conn '[{:astro-scene/coordinate [:coordinate/type
                                                         :astronomical-coordinate/center-object
                                                         :terrestrial-coordinate/center-object
                                                         :horizon-coordinate/center-object]}]
                         (:db/id scene))
        coordinate-1 (:astro-scene/coordinate scene-1)]
    (case (get-in coordinate-1 [:coordinate/type])
      :horizon-coordinate (get-in coordinate-1 [:horizon-coordinate/center-object])
      :terrestrial-coordinate (get-in coordinate-1 [:terrestrial-coordinate/center-object])
      :astronomical-coordinate (get-in coordinate-1 [:astronomical-coordinate/center-object]))))

;; 

(def find-center-celestial-id-query
  '[:find ?cele-id .
    :in $ ?scene-id
    :where
    [?scene-id :astro-scene/coordinate ?coor-id]
    [?coor-id :coordinate/track-position ?cele-id]])


;; tx

(defn set-scene-coordinate-tx [scene-sm coordinate-sm]
  [{:db/id (:db/id scene-sm)
    :astro-scene/coordinate (:db/id coordinate-sm)}])

(defn refresh-tx [db1 astro-scene]
  (let [clock-id (get-in astro-scene [:astro-scene/clock :db/id])
        celes (m.celestial/find-all-by-clock db1 clock-id)
        tx1 (mapcat #(m.celestial/update-current-position-and-quaternion-tx %) celes)
        db2 (d/db-with db1 tx1)
        coor-ids (m.coordinate/find-all-ids db2)
        tx2 (mapcat (fn [id]
                      (let [coor (d/pull db2 '[:db/id :entity/type] id)]
                        (m.coordinate/update-position-and-quaternion-tx db2 coor)))
                    coor-ids)]
    (concat tx1 tx2)))

