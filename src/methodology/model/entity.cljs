(ns methodology.model.entity
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]
   [posh.reagent :as p]))


;; 最基础的抽象，有 :db/id :entity/name :entity/type，可以是抽象或实体的概念

(def sample-1 {:db/id 1
               :entity/name "solar"
               :entity/type :star})

(def schema {:entity/name {:db/unique :db.unique/identity}
             :entity/chinese-name {:db/unique :db.unique/identity}})

;; spec

(s/def :methodology/entity
  (s/keys :req [:db/id]))

;; model 


(comment
  (s/valid? :methodology/entity {:db/id 4} )
  )