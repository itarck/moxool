(ns astronomy.objects.planet.h
  (:require
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.objects.planet.m :as planet]
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


(defmethod handle-event :planet/show-name
  [props _env {:event/keys [detail]}]
  (let [{:keys [planet show?]} detail
        tx [{:db/id (:db/id planet)
             :planet/show-name? show?}]]
    (effects :tx tx)))

(defmethod handle-event :planet/update-all-world-position
  [_props {:keys [db]} _event]
  (let [tx (planet/update-all-world-position db)]
    (effects :tx tx
             :event #:event{:action :planet/update-all-position-logs})))

(defmethod handle-event :planet/update-all-local-position
  [{:keys [astro-scene]} {:keys [db]} _event]
  (let [coordinate (m.astro-scene/pull-scene-coordinate db astro-scene)]
    (effects :tx (planet/update-all-local-position db coordinate)
             :event #:event{:action :planet/update-all-position-logs})))


(defmethod handle-event :planet/update-all-position-logs
  [_props {:keys [db]} _event]
  (effects :tx (planet/update-all-position-logs db)))

;; listen

(defmethod handle-event :clock.pub/time-changed
  [_props _env {:event/keys [detail] :as event}]
  (effects :event #:event{:action :planet/update-all-local-position}))