(ns laboratory.parts.framework.schema
  (:require
   [fancoil.module.posh.base :as posh.base]))


(defmethod posh.base/schema :framework/schema
  [_ _]
  {:framework/name {:db/unique :db.unique/identity}
   :framework/scene {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})