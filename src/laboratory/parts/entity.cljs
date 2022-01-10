(ns laboratory.parts.entity
  (:require
   [posh.reagent :as p]
   [fancoil.base :as base]
   [cljs.spec.alpha :as s]))


;; spec

(defmethod base/spec :db/id
  [_ _]
  (s/def :db/id int?))

;; subscribe

(defmethod base/subscribe :entity/pull
  [{:keys [pconn]} _ {id :id pattern :pattern :or {pattern '[*]}}]
  (p/pull pconn pattern id))
