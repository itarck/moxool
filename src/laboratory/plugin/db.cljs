(ns laboratory.plugin.db
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [laboratory.base :as base]
   [cljs.spec.alpha :as s]))


;; spec

(defmethod base/spec :db/spec
  [_ _]
  (s/def :db/id (s/or :id int? :lookup-ref vector?))
  (s/def :db/entity (s/keys :req [:db/id])))


;; model 

(defmethod base/model :db/pull
  [_ _ props]
  (let [{:keys [id pattern db] :or {pattern '[*]}} props]
    (d/pull db pattern id)))

;; subscribe

(defmethod base/subscribe :db/pull
  [{:keys [pconn]} _ {id :id pattern :pattern :or {pattern '[*]}}]
  (p/pull pconn pattern id))

(defmethod base/subscribe :entity/pull
  [{:keys [pconn]} _ {:keys [entity]}]
  (p/pull pconn '[*] (:db/id entity)))
