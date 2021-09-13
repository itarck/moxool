(ns astronomy.objects.astronomical-coordinate.h
  (:require
   [datascript.core :as d]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.objects.astronomical-coordinate.m :as ac.m]
   [astronomy.service.effect :as s.effect :refer [create-effect effects]]))


(defmulti handle-event (fn [_props _env event] (:event/action event)))


(defmethod handle-event :astronomical-coordinate/change-show-longitude
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-longitude? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate/change-show-latitude
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-latitude? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate/change-show-latitude-0
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-latitude-0? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate/change-show-regression-line
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-regression-line? show?}]]
    (create-effect :tx tx)))


(defmethod handle-event :astronomical-coordinate/change-show-longitude-0
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-longitude-0? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate/change-show-marks
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-marks? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate/change-show-ecliptic
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-ecliptic? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate/change-show-lunar-orbit
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-lunar-orbit? show?}]]
    (create-effect :tx tx)))


(defmethod handle-event :astronomical-coordinate/change-radius
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate radius]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/radius radius}]]
    (create-effect :tx tx)))


(defmethod handle-event :astronomical-coordinate/change-center-object
  [{:keys [astro-scene]} {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [coordinate center-object]} detail
        tx (ac.m/change-center-object-tx db coordinate center-object)
        astro-scene-1 (d/pull db '[*] (:db/id astro-scene))
        event #:event{:action :astro-scene.pub/coordinate-changed
                      :detail {:astro-scene astro-scene
                               :coordinate coordinate}}]
    (if (m.astro-scene/is-scene-coordinate? astro-scene-1 coordinate)
      (effects :tx tx :event event)
      (effects :tx tx))))