(ns laboratory.parts.db
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [fancoil.base :as base]
   [cljs.spec.alpha :as s]))


;; spec

(defmethod base/spec :db/id
  [_ _ _]
  (s/def :db/id
    (s/or :id int?
          :lookup-ref vector?)))

(defmethod base/spec :db/entity
  [_ _ _]
  (base/spec {} :db/id)
  (s/def :db/entity
    (s/keys :req [:db/id])))

;; model 

(defmethod base/model :db/pull
  [_ _ props]
  (let [{:keys [id pattern db] :or {pattern '[*]}} props]
    (d/pull db pattern id)))

;; subscribe

(defmethod base/subscribe :db/pull
  [{:keys [pconn]} _ {id :id pattern :pattern :or {pattern '[*]}}]
  (p/pull pconn pattern id))

