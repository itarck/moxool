(ns astronomy.model.coordinate
  (:require 
   [astronomy.model.astronomical-coordinate :as m.astronomical-coordinate]))


(def schema {:coordinate/name {:db/unique :db.unique/identity}})

(comment
  (def sample
    #:coordinate {:object/position [0 0 0]
                  :object/quaternion [0 0 0 1]
                  :type :astronomical-coordinate})
  
  )


(defn update-position-and-quaternion-tx [db coordiante]
  (case (:coordinate/type coordiante)
    :astronomical-coordinate (m.astronomical-coordinate/update-position-tx db coordiante)))