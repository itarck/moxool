(ns astronomy.model.user.coordinate-tool
  (:require
   [posh.reagent :as p])
  )


(def schema {:coordinate-tool/coordinate {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})


(comment
  
  (def coordinate-tool-1
    #:coordinate-tool{:coordinate [:coordinate/name "default"]
                      :tool/name "coordinate tool"
                      :tool/chinese-name "坐标系设置工具"
                      :tool/icon "/image/pirate/earth.jpg"
                      :tool/backpack {:backpack/name "default"}

                      :entity/type :coordinate-tool})
  ;; 
  )



(defn sub-one [conn id]
  @(p/pull conn '[* {:coordinate-tool/coordinate [*]}] id))