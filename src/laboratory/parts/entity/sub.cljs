(ns laboratory.parts.entity.sub
  (:require
   [posh.reagent :as p]
   [fancoil.base :as base]))


(defmethod base/subscribe :entity/pull
  [{:keys [pconn]} _ {id :id pattern :pattern :or {pattern '[*]}}]
  (p/pull pconn pattern id))