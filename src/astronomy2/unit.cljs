(ns astronomy2.unit
  (:require 
   [cljs.pprint :refer [pprint]]
   [fancoil.base :as base]))


(defmethod base/process :default
  [{:keys [do! handle inject]} method req]
  (pprint "handle input: ")
  (pprint req)
  (let [req (inject :posh/db req)
        resp (handle method req)]
    (pprint "handle output: ")
    (pprint resp)
    (do! :do/effect resp)))