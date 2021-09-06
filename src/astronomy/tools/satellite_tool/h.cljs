(ns astronomy.tools.satellite-tool.h
  (:require
   [astronomy.tools.satellite-tool.m :as satellite-tool.m]
   [astronomy.service.effect :refer [effects]]))


(defmulti handle-event (fn [props env event] (:event/action event)))

(defmethod handle-event :satellite-tool/log
  [_props _env {:event/keys [detail]}]
  #:effect {:action :log :detail detail})

(defmethod handle-event :satellite-tool/change-target
  [props _env {:event/keys [detail]}]
  (let [{:keys [new-satellite-id satellite-tool]} detail
        tx (satellite-tool.m/set-target-tx satellite-tool new-satellite-id)]
    (effects :tx tx)))
