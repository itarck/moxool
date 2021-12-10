(ns astronomy.objects.satellite.h
  (:require
   [astronomy.objects.celestial.m :as m.celestial]
   [astronomy.objects.satellite.m :as satellite.m]
   [astronomy.service.effect :refer [effects]]))


(defmulti handle-event (fn [props env event] (:event/action event)))


(defmethod handle-event :satellite/change-show-orbit
  [props _env {:event/keys [detail]}]
  (let [{:keys [celestial show?]} detail
        tx (m.celestial/update-show-orbit-tx celestial show?)]
    (effects :tx tx)))

(defmethod handle-event :satellite/show-moon-orbit-helper-lines?
  [props _env {:event/keys [detail]}]
  (let [{:keys [celestial show?]} detail
        tx [{:db/id (get-in celestial [:celestial/orbit :db/id])
             :moon-orbit/show-helper-lines? show?}]]
    (effects :tx tx)))

(defmethod handle-event :satellite/change-in-scene?
  [props _env {:event/keys [detail]}]
  (let [{:keys [satellite in-scene?]} detail
        tx (satellite.m/change-in-scene? satellite in-scene?)]
    (effects :tx tx)))