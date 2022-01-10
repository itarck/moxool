(ns laboratory.parts.entity
  (:require
   [posh.reagent :as p]
   [fancoil.base :as base]
   [cljs.spec.alpha :as s]))


;; spec

(defmethod base/spec :db/id
  [_ _ _]
  (s/def :db/id
    (s/or :id int?
          :lookup-ref vector?)))

(defmethod base/spec :entity/id
  [_ _ _]
  (s/def :entity/id
    (s/or :id int?
          :lookup-ref vector?)))

(defmethod base/spec :entity/model
  [_ _ _]
  (base/spec {} :db/id)
  (s/def :entity/model
    (s/keys :req [:db/id])))

;; subscribe

(defmethod base/subscribe :entity/pull
  [{:keys [pconn]} _ {id :id pattern :pattern :or {pattern '[*]}}]
  (p/pull pconn pattern id))

