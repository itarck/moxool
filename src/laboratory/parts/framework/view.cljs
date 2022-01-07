(ns laboratory.parts.framework.view
  (:require
   [fancoil.base :as base]))


(defmethod base/view :framework/view
  [{:keys [subscribe] :as core} _signal props]
  (let [fw @(subscribe :entity/pull {:id (:db/id props)})]
    [:<>
     [base/view core :scene/view (:framework/scene fw)]]))

