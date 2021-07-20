(ns astronomy.model.atmosphere
  (:require
   [posh.reagent :as p]))


(def schema {:atmosphere/name {:db/unique :db.unique/identity}})



(comment
  (def sample-1
    #:atmosphere {:name "default"
                  :show? true
                  :object/scene [:scene/name "solar"]
                  :entity/type :atmosphere})
;;   
  )


(def whole-selector
  '[* 
    {:atmosphere/coordinate [*]}])

(def unique-id
  [:atmosphere/name "default"])

(defn sub-unique-one [conn]
  @(p/pull conn whole-selector unique-id))

