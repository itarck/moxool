(ns astronomy.model.astro-scene
  (:require
   [datascript.core :as d]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.model.coordinate :as m.coordinate]
   [posh.reagent :as p]))


(def sample
  #:astro-scene {:astro-scene/coordinate {:db/id 100}
                 :scene/name "astronomy"
                 :scene/chinese-name "天文学场景"
                 :object/_scene [{:db/id 10} {:db/id 30}]})


(def schema
  {:astro-scene/coordinate {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


;; tx


;; 当时间变化的时候，先更新时钟，再更新恒星、行星、卫星位置，最后更新参考系

(defn update-by-clock-tx [db1 astro-scene-id clock-id]
  (let [celes (m.celestial/find-all-by-clock db1 clock-id)
        tx1 (mapcat #(m.celestial/update-position-and-quaternion-tx %) celes)
        db2 (d/db-with db1 tx1)
        astro-scene (d/pull db2 '[*] astro-scene-id)
        coordinate-id (:db/id (:astro-scene/coordinate astro-scene))
        tx2 (m.coordinate/update-coordinate-tx db2 coordinate-id)]
    (concat tx1 tx2)))