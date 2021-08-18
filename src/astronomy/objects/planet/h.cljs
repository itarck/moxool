(ns astronomy.objects.planet.h
  (:require
   [astronomy.model.celestial :as m.celestial]
   [astronomy.service.effect :refer [effects]]))


(defmulti handle-event (fn [props env event] (:event/action event)))


(defmethod handle-event :planet/show-orbit
  [props _env {:event/keys [detail]}]
  (let [{:keys [celestial show?]} detail
        tx (m.celestial/update-show-orbit-tx celestial show?)]
    (effects :tx tx)))


(defmethod handle-event :planet/show-spin-helper
  [props _env {:event/keys [detail]}]
  (let [{:keys [celestial show?]} detail
        tx (m.celestial/update-show-spin-helper-tx celestial show?)]
    (effects :tx tx)))
