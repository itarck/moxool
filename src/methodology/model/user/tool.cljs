(ns methodology.model.user.tool)

;; 抽象类

(def schema
  {:tool/name {:db/unique :db.unique/identity}
   :tool/backpack {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})