(ns astronomy.model.coordinate
  (:require 
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.model.astronomical-coordinate :as m.astronomical-coordinate]
   [astronomy.model.terrestrial-coordinate :as m.terrestrial-coordinate]))


(def schema {:coordinate/name {:db/unique :db.unique/identity}})

(comment
  (def sample
    #:coordinate {:name "赤道天球坐标系"
                  :object/position [0 0 0]
                  :object/quaternion [0 0 0 1]
                  :type :astronomical-coordinate})
  
  )

;; find 

(defn find-all-ids [db]
  (d/q '[:find [?id ...]
         :where [?id :coordinate/name _]]
        db))

;; sub

(defn sub-all-coordinate-names [conn]
  @(p/q '[:find [?name ...]
          :where [?id :coordinate/name ?name]]
        conn))

;; tx

(defn update-position-and-quaternion-tx [db coordinate-id]
  (let [coordinate (d/pull db '[*] coordinate-id)]
    (case (:coordinate/type coordinate)
      :astronomical-coordinate (m.astronomical-coordinate/update-position-and-quaternion-tx db coordinate-id)
      :terrestrial-coordinate (m.terrestrial-coordinate/update-position-and-quaternion-tx db coordinate-id))))