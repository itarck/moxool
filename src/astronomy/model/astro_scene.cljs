(ns astronomy.model.astro-scene
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [shu.geometry.angle :as shu.angle]
   [astronomy.model.coordinate :as m.coordinate]))


(def sample
  #:astro-scene {:astro-scene/coordinate {:db/id 100}
                 :astro-scene/clock [:clock/name "default"]
                 :astro-scene/camera [:camera/name "default"]
                 :scene/name "astronomy"
                 :scene/chinese-name "天文学场景"
                 :object/_scene [{:db/id 10} {:db/id 30}]})


(def schema
  #:astro-scene{:coordinate {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
                :reference {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
                :clock {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
                :camera {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


(defn sub-scene-with-objects [conn id]
  @(p/pull conn '[* {:object/_scene [*]}] id))


(defn sub-scene-name-exist? [conn scene-name]
  (seq
   @(p/q '[:find [?id ...]
           :in $ ?scene-name
           :where
           [?id :scene/name ?scene-name]]
         conn scene-name)))

(def find-center-celestial-id-query
  '[:find ?cele-id .
    :in $ ?scene-id
    :where
    [?scene-id :astro-scene/coordinate ?coor-id]
    [?coor-id :coordinate/track-position ?cele-id]])


#_(defn has-day-light?
  ([coordinate camera-control atmosphere]
   (has-day-light? coordinate camera-control atmosphere 0.55))
  ([coordinate camera-control atmosphere angle-limit]
   (let [sun-position (m.coordinate/original-position coordinate)
         {:spaceship-camera-control/keys [up]} camera-control
         angle (v3/angle-to (v3/from-seq up) sun-position)
         has-day-light (and
                        (:atmosphere/show? atmosphere)
                        (= :surface-control (:spaceship-camera-control/mode camera-control))
                        (< angle (* angle-limit Math/PI)))]
     has-day-light)))

(defn has-day-light?
  ([sun-position camera-control atmosphere]
   (has-day-light? sun-position camera-control atmosphere 0.55))
  ([sun-position camera-control atmosphere angle-limit]
   (let [{:spaceship-camera-control/keys [up]} camera-control
         angle (v3/angle-to (v3/from-seq up) sun-position)
         has-day-light (and
                        (:atmosphere/show? atmosphere)
                        (= :surface-control (:spaceship-camera-control/mode camera-control))
                        (< angle (* angle-limit Math/PI)))]
     has-day-light)))

;; tx

