(ns astronomy.tools.astronomical-coordinate-tool.h
  (:require
   [astronomy.tools.astronomical-coordinate-tool.m :as astronomical-coordinate-tool]
   [astronomy.service.effect :as s.effect :refer [create-effect effects]]))


;; handle-event version

(defmulti handle-event (fn [_props _env event] (:event/action event)))

(defmethod handle-event :astronomical-coordinate-tool/log
  [_props _env {:event/keys [detail]}]
  #:effect {:action :log :detail detail})

(defmethod handle-event :astronomical-coordinate-tool/change-query-args
  [props {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [tool query-args]} detail]
    (create-effect :tx (astronomical-coordinate-tool/update-query-args-tx db tool query-args))))
