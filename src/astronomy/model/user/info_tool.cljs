(ns astronomy.model.user.info-tool
  (:require
   [posh.reagent :as p]))


(def sample-1
  #:info-tool {:object {:db/id -100}
               :tool/name "info tool"
               :tool/chinese-name "信息查询工具"
               :tool/icon "/image/pirate/cow.jpg"
               :tool/backpack {:backpack/name "default"}
               :entity/type :info-tool})


(def schema
  #:info-tool{:object {:db/valueType :db.type/ref
                       :db/cardinality :db.cardinality/one}})


(defn sub-info-tool [conn id]
  @(p/pull conn '[* {:info-tool/object [*]}] id))