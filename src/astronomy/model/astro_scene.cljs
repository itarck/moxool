(ns astronomy.model.astro-scene)


(def sample
  #:astro-scene {:scene/name "astronomy"
                 :scene/chinese-name "天文学场景"})


(def schema
  {:astro-scene/coordinate {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})