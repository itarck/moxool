(ns astronomy.model.astro-scene
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]))


(def sample
  #:astro-scene {:astro-scene/coordinate {:db/id 100}
                 :scene/name "astronomy"
                 :scene/chinese-name "天文学场景"
                 :object/_scene [{:db/id 10} {:db/id 30}]})


(def schema
  {:astro-scene/coordinate {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


;; tx

