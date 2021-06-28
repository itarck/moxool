(ns astronomy.model.atmosphere
  (:require
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [astronomy.model.coordinate :as m.coordinate]))


(def schema {:atmosphere/name {:db/unique :db.unique/identity}
             :atmosphere/coordinate {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


(comment
  (def sample-1
    #:atmosphere {:name "default"
                  :show? true
                  :coordinate [:coordinate/name "default"]
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


(defn sun-position-vector [atmosphere]
  (m.coordinate/original-position (:atmosphere/coordinate atmosphere)))

