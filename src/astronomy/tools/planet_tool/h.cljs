(ns astronomy.tools.planet-tool.h
  (:require
   [astronomy.tools.planet-tool.m :as planet-tool.m]
   [astronomy.service.effect :refer [effects]]))


(defmulti handle-event (fn [props env event] (:event/action event)))


(defmethod handle-event :planet-tool/change-target
  [props _env {:event/keys [detail]}]
  (let [{:keys [new-planet-id planet-tool]} detail
        tx (planet-tool.m/set-target-tx planet-tool new-planet-id)]
    (effects :tx tx)))
