(ns laboratory.parts.object.schema
  (:require
   [fancoil.module.posh.base :as posh.base]))


(defmethod posh.base/schema :object/schema
  [_ _]
  {:object/name {:db/unique :db.unique/identity}
   :object/scene {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})