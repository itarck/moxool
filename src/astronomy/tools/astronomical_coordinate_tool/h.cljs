(ns astronomy.tools.astronomical-coordinate-tool.h
  (:require
   [datascript.core :as d]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.objects.astronomical-coordinate.m :as ac.m]
   [astronomy.tools.astronomical-coordinate-tool.m :as astronomical-coordinate-tool]
   [astronomy.service.effect :as s.effect :refer [create-effect effects]]))


;; handle-event version

(defmulti handle-event (fn [_props _env event] (:event/action event)))

(defmethod handle-event :astronomical-coordinate-tool/log
  [_props _env {:event/keys [detail]}]
  #:effect {:action :log :detail detail})

(defmethod handle-event :astronomical-coordinate-tool/change-show-longitude
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-longitude? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate-tool/change-show-latitude
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-latitude? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate-tool/change-query-args
  [props {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [tool query-args]} detail]
    (create-effect :tx (astronomical-coordinate-tool/update-query-args-tx db tool query-args))))

(defmethod handle-event :astronomical-coordinate-tool/change-show-latitude-0
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-latitude-0? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate-tool/change-show-regression-line
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-regression-line? show?}]]
    (create-effect :tx tx)))


(defmethod handle-event :astronomical-coordinate-tool/change-show-longitude-0
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-longitude-0? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate-tool/change-show-marks
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-marks? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate-tool/change-show-ecliptic
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-ecliptic? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate-tool/change-show-lunar-orbit
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-lunar-orbit? show?}]]
    (create-effect :tx tx)))


(defmethod handle-event :astronomical-coordinate-tool/change-radius
  [_props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate radius]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/radius radius}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate-tool/change-center-object
  [{:keys [astro-scene]} {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [coordinate center-object]} detail
        tx (ac.m/change-center-object-tx db coordinate center-object)
        astro-scene-1 (d/pull db '[*] (:db/id astro-scene))
        event #:event{:action :astro-scene.pub/coordinate-changed
                      :detail {:astro-scene astro-scene
                               :coordinate coordinate}}]
    (println "!!!astronomical-coordinate-tool/change-center-object: " tx)
    (if (m.astro-scene/is-scene-coordinate? astro-scene-1 coordinate)
      (effects :tx tx :event event)
      (effects :tx tx))))