(ns laboratory.plugin.db
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [laboratory.base :as base]
   [cljs.spec.alpha :as s]))


;; spec

(defmethod base/spec :db/spec
  [_ _]
  (s/def :db/id (s/or :id int? :lookup-ref vector?)))


;; model 

(defmethod base/model :db/pull
  [_ _ props]
  (let [{:keys [id pattern db] :or {pattern '[*]}} props]
    (d/pull db pattern id)))

;; subscribe

(defmethod base/subscribe :db/pull
  [{:keys [pconn]} _ {id :id pattern :pattern :or {pattern '[*]}}]
  (p/pull pconn pattern id))

