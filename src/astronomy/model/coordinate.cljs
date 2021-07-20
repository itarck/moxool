(ns astronomy.model.coordinate
  (:require 
   [posh.reagent :as p]
   [astronomy.model.astronomical-coordinate :as m.astronomical-coordinate]))


(def schema {:coordinate/name {:db/unique :db.unique/identity}})

(comment
  (def sample
    #:coordinate {:name "赤道天球坐标系"
                  :object/position [0 0 0]
                  :object/quaternion [0 0 0 1]
                  :type :astronomical-coordinate})
  
  )

;; sub

(defn sub-all-coordinate-names [conn]
  @(p/q '[:find [?name ...]
          :where [?id :coordinate/name ?name]]
        conn))

;; tx

(defn update-position-and-quaternion-tx [db coordinate]
  (case (:coordinate/type coordinate)
    :astronomical-coordinate (m.astronomical-coordinate/update-position-tx db coordinate)))