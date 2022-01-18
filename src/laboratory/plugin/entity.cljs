(ns laboratory.plugin.entity
  (:require
   [posh.reagent :as p]
   [laboratory.base :as base]
   [cljs.spec.alpha :as s]))

;; schema

(defmethod base/schema :entity/schema
  [_ _]
  {:entity/name {:db/unique :db.unique/identity}})

;; spec

(defmethod base/spec :entity/spec
  [_ _]
  (s/def :db/lookup-ref (s/tuple keyword?
                                 (s/or :keyword keyword?
                                       :string string?
                                       :number number?)))
  (s/def :db/id (s/or :id int?
                      :lookup-ref :db/lookup-ref))
  (s/def :entity/name string?)
  (s/def :entity/entity (s/keys :req [:db/id])))


;; subscribe

(defmethod base/subscribe :entity/pull
  [{:keys [pconn]} _ {:keys [entity]}]
  (p/pull pconn '[*] (:db/id entity)))


