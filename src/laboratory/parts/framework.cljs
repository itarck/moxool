(ns laboratory.parts.framework
  (:require
   [fancoil.base :as base]
   [fancoil.module.posh.base :as posh.base]))

;; schema

(defmethod posh.base/schema :framework/schema
  [_ _]
  {:framework/name {:db/unique :db.unique/identity}
   :framework/scene {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}
   :framework/user {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})

;; model

(defmethod base/model :framework/create
  [_ _ props]
  (let [default #:framework {:name "default"}]
    (merge default props)))

;; view

(defmethod base/view :framework/view
  [{:keys [subscribe] :as core} _signal props]
  (let [fw @(subscribe :entity/pull {:id (:db/id props)})]
    [:<>
     [base/view core :scene/view (:framework/scene fw)]
     [base/view core :user/view (:framework/user fw)]]))

