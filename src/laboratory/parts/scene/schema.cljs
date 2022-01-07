(ns laboratory.parts.scene.schema
  (:require 
   [fancoil.module.posh.base :as posh.base]))


(defmethod posh.base/schema :scene/schema
  [_ _]
  {:scene/name {:db/unique :db.unique/identity}})