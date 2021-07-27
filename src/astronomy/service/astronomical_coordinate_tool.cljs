(ns astronomy.service.astronomical-coordinate-tool
  (:require
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.user.astronomical-coordinate-tool :as m.astronomical-coordinate-tool]
   [astronomy.service.effect :as s.effect :refer [create-effect]]))


;; handle-event version

(defmulti handle-event (fn [props env event] (:event/action event)))

(defmethod handle-event :astronomical-coordinate-tool/log
  [props {:keys [conn]} {:event/keys [detail]}]
  #:effect {:action :log :detail detail})

(defmethod handle-event :astronomical-coordinate-tool/change-show-longitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-longitude? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate-tool/change-show-latitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-latitude? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate-tool/set-scene-reference
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate]} detail
        astro-scene (get-in props [:astro-scene])]
    (create-effect :tx (m.astro-scene/set-scene-coordinate-tx astro-scene astronomical-coordinate))))

(defmethod handle-event :astronomical-coordinate-tool/change-query-args
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool query-args]} detail]
    (create-effect :tx (m.astronomical-coordinate-tool/update-query-args-tx @conn tool query-args))))

(defmethod handle-event :astronomical-coordinate-tool/change-show-latitude-0
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-latitude-0? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate-tool/change-show-regression-line
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-regression-line? show?}]]
    (create-effect :tx tx)))


(defmethod handle-event :astronomical-coordinate-tool/change-show-longitude-0
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-longitude-0? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate-tool/change-show-ecliptic
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-ecliptic? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate-tool/change-show-lunar-orbit
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-lunar-orbit? show?}]]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-coordinate-tool/object-clicked
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate clicked-point meta-key current-tool]} detail
        current-coordinate-id (-> current-tool :astronomical-coordinate-tool/query-result first)]
    ;; (println "service :astronomical-coordinate-tool/object-clicked")
    (when (= current-coordinate-id (:db/id astronomical-coordinate))
      (create-effect :tx [{:db/id current-coordinate-id
                           :astronomical-coordinate/current-point (vec clicked-point)}]))))

