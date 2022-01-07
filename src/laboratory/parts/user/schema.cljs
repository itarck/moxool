(ns laboratory.parts.user.schema
  (:require
   [fancoil.module.posh.base :as posh.base]))


(defmethod posh.base/schema :user/schema
  [_ _]
  {:user/name {:db/unique :db.unique/identity}
   :user/backpack {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/mouse {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/camera-control {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/right-tool {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})