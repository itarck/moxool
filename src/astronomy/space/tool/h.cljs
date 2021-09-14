(ns astronomy.space.tool.h
  (:require
   [astronomy.service.effect :as s.effect :refer [create-effect]]))


;; handle-event version

(defmulti handle-event (fn [props env event] (:event/action event)))

(defmethod handle-event :tool/log
  [_props _env {:event/keys [detail]}]
  (create-effect :log detail))

(defmethod handle-event :tool/change-panel
  [_props _env {:event/keys [detail]}]
  (let [{:keys [tool current-panel]} detail
        tx [{:db/id (:db/id tool)
             :tool/current-panel current-panel}]]
    (create-effect :tx tx)))