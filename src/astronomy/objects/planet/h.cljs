(ns astronomy.objects.planet.h
  (:require
   [astronomy.objects.astro-scene.m :as m.astro-scene]
   [astronomy.objects.celestial.m :as m.celestial]
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

(defmethod handle-event :planet/change-track-position
  [props _env {:event/keys [detail]}]
  (let [{:keys [planet track-position?]} detail
        tx [{:db/id (:db/id planet)
             :planet/track-position? track-position?
             :planet/position-log []}]]
    (effects :tx tx)))

(defmethod handle-event :planet/change-show-tracks
  [props _env {:event/keys [detail]}]
  (let [{:keys [planet show-tracks?]} detail
        tx [{:db/id (:db/id planet)
             :planet/show-tracks? show-tracks?}]]
    (effects :tx tx)))

(defmethod handle-event :planet/change-show-epicycle
  [props _env {:event/keys [detail]}]
  (let [{:keys [planet show-epicycle?]} detail
        tx [{:db/id (:db/id planet)
             :planet/show-epicycle? show-epicycle?}]]
    (effects :tx tx)))

(defmethod handle-event :planet/change-scale
  [props _env {:event/keys [detail]}]
  (let [{:keys [planet scale]} detail
        tx [{:db/id (:db/id planet)
             :celestial/scale scale}]]
    (effects :tx tx)))

(defmethod handle-event :planet/update-all-position-logs
  [{:keys [astro-scene]} {:keys [db]} {:event/keys [detail] :as event}]
  (let [{:keys [clock]} detail
        coordinate (m.astro-scene/pull-scene-coordinate db astro-scene)]
    (effects :tx (planet/update-all-position-logs db coordinate clock))))

;; listen

(defmethod handle-event :clock.pub/time-changed
  [_props _env {:event/keys [detail] :as event}]
  (effects :event #:event{:action :planet/update-all-position-logs
                          :detail detail}))