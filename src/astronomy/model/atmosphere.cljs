(ns astronomy.model.atmosphere
  (:require
   [posh.reagent :as p]))


(def schema {:atmosphere/name {:db/unique :db.unique/identity}})


(defn has-day-light? [atmosphere sun-elevation-angle]
  (and
   (:atmosphere/show? atmosphere)
   (>= sun-elevation-angle 0)))

(defn show-atmosphere? [atmosphere sun-elevation-angle]
  (and
   (:atmosphere/show? atmosphere)
   (>= sun-elevation-angle -10)))


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

