(ns methodology.model.camera
  (:require
   [datascript.core :as d]))



(def schema {:camera/name  {:db/unique :db.unique/identity}})


(def camera-1
  #:camera{:name "default"
           :position [0 0 20]
           :quaternion [0 0 0 1]})


;; 

(defn pull-unique-one [db]
  (d/pull db '[*] [:camera/name "default"]))