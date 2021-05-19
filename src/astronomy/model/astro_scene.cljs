(ns astronomy.model.astro-scene
  (:require
   [datascript.core :as d]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.model.coordinate :as m.ref]
   [posh.reagent :as p]))


(def sample
  #:astro-scene {:astro-scene/coordinate {:db/id 100}
                 :scene/name "astronomy"
                 :scene/chinese-name "天文学场景"
                 :object/_scene [{:db/id 10} {:db/id 30}]})


(def schema
  {:astro-scene/coordinate {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


;; tx

(defn update-celestials-by-clock-tx [db clock-id]
  (let [celes (m.celestial/find-all-by-clock db clock-id)]
    (mapcat #(m.celestial/update-position-and-quaternion-tx %) celes)))


(defn update-reference-tx [db]
  (let [id [:coordinate/name "default"]]
    [[:db/add id :coordinate/position (m.ref/cal-world-position db id)]
     [:db/add id :coordinate/quaternion (m.ref/cal-world-quaternion db id)]]))
