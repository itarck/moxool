(ns methodology.model.scene
  (:require 
   [posh.reagent :as p]))


(def sample-1 
  #:scene{:name "solar"
          :chinese-name "太阳系模型"})


(def schema {:scene/name {:db/unique :db.unique/identity}})


;; model

(defn sub-objects [conn scene-id]
  (let [scene @(p/pull conn '[{:object/_scene [*]}] scene-id)]
    (:object/_scene scene)))


