(ns laboratory.plugin.framework
  (:require
   [laboratory.base :as base]))

;; schema

(defmethod base/schema :framework/schema
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
  [{:keys [subscribe] :as core} _signal entity]
  (let [fw @(subscribe :entity/pull {:entity entity})
        {:framework/keys [scene user]} fw]
    [:div.h-full.w-full
     (when scene
       [base/view core :scene/view (:framework/scene fw)])
     (when user
       [base/view core :user/view (:framework/user fw)])]))

