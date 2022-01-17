(ns laboratory.plugin.entity
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [laboratory.base :as base]
   [cljs.spec.alpha :as s]))


;; spec

(defmethod base/spec :entity/spec
  [_ _]
  (s/def :db/id (s/or :id int? :lookup-ref vector?))
  (s/def :entity/entity (s/keys :req [:db/id])))


;; subscribe

(defmethod base/subscribe :entity/pull
  [{:keys [pconn]} _ {:keys [entity]}]
  (p/pull pconn '[*] (:db/id entity)))
