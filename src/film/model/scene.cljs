(ns film.model.scene
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :as async :refer [go >! <! chan alts! timeout put! take! go-loop]]
   [posh.reagent :as p]))


;; model

(def sample 
  #:scene {:db/id -101
           :name "city"})


(def schema {:scene/name {:db/unique :db.unique/identity}})


(defn sub-scene [system-conn id]
  @(p/pull system-conn '[*] id))

(defn sub-whole-scene [system-conn scene-id]
  @(p/pull system-conn '[{:video/_scene [*]} *] scene-id))

