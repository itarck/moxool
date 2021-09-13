(ns astronomy.objects.satellite.h
  (:require
   [astronomy.objects.celestial.m :as m.celestial]
   [astronomy.service.effect :refer [effects]]))


(defmulti handle-event (fn [props env event] (:event/action event)))


(defmethod handle-event :satellite/change-show-orbit
  [props _env {:event/keys [detail]}]
  (let [{:keys [celestial show?]} detail
        tx (m.celestial/update-show-orbit-tx celestial show?)]
    (effects :tx tx)))

(defmethod handle-event :satellite/change-show-object
  [props _env {:event/keys [detail]}]
  (let [{:keys [celestial show?]} detail
        tx [{:db/id (:db/id celestial)
             :object/show? show?}]]
    (effects :tx tx)))