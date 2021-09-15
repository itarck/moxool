(ns astronomy.space.selector.h
  (:require
   [astronomy.service.effect :as s.effect :refer [create-effect effects]]))


;; handle-event version

(defmulti handle-event (fn [_props _env event] (:event/action event)))

(defmethod handle-event :selector/log
  [_props _env {:event/keys [detail]}]
  #:effect {:action :log :detail detail})

(defmethod handle-event :selector/select
  [_props _env {:event/keys [detail]}]
  (let [{:keys [selector selected]} detail
        tx [{:db/id (:db/id selector)
             :selector/selected selected}]]
    (effects :tx tx)))
