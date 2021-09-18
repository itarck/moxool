(ns film2.modules.studio.h
  (:require
   [astronomy.service.effect :as s.effect :refer [effects]]))


(defmulti handle-event (fn [_p _e event] (:event/action event)))

(defmethod handle-event :studio/change-mode [_p _e {:event/keys [detail]}]
  (let [{:keys [studio new-mode]} detail
        tx [{:db/id (:db/id studio)
             :studio/mode new-mode}]]
    (effects :tx tx
             :log tx)))