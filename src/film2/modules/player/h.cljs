(ns film2.modules.player.h
  (:require
   [astronomy.service.effect :as s.effect :refer [effects]]))


(defmulti handle-event (fn [_p _e event] (:event/action event)))


(defmethod handle-event :player/log [_p _e {:event/keys [detail]}]
  (effects :log detail))


(defmethod handle-event :player/change-current-iovideo [_p _e {:event/keys [detail]}]
  (let [{:keys [player iovideo]} detail
        tx [{:db/id (:db/id player)
             :player/current-iovideo (:db/id iovideo)}]]
    (effects :tx tx)))