(ns methodology.model.user.tool)

;; 抽象类

(def schema
  {:tool/name {:db/unique :db.unique/identity}
   :tool/backpack {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :tool/target {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


(def sample
  #:tool{:query-one-type :by-name
         :query-one-candinates []
         :query-one-args []

         :query-many-type :by-group
         :query-many-candinates []
         :query-many-args []
         :query-many-result []

         :tool/name "constellation-tool"
         :tool/chinese-name "星座"
         :tool/icon "/image/pirate/earth.jpg"
         :tool/type :constellation-tool
         :entity/type :constellation-tool})