(ns laboratory.parts.scene.sub
  (:require
   [posh.reagent :as p]
   [fancoil.base :as base]))


(defmethod base/subscribe :scene/pull-one
  [{:keys [pconn]} _ {:db/keys [id]}]
  (p/pull pconn '[* {:object/_scene [*]}] id))