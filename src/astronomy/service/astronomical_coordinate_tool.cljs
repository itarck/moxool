(ns astronomy.service.astronomical-coordinate-tool
  (:require
   [datascript.core :as d]
   [shu.three.vector3 :as v3]
   [astronomy.model.object :as m.object]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.user.astronomical-coordinate-tool :as m.astronomical-coordinate-tool]
   [astronomy.service.effect :as s.effect :refer [create-effect]]))


;; handle-event version

(defmulti handle-event (fn [props env event] (:event/action event)))

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

(defmethod handle-event :astronomical-coordinate-tool/set-scene-reference
  [props _env {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate]} detail
        astro-scene (get-in props [:astro-scene])]
    (create-effect :tx (m.astro-scene/set-scene-coordinate-tx astro-scene astronomical-coordinate))))

(defmethod handle-event :astronomical-coordinate-tool/change-query-args
  [props {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [tool query-args]} detail]
    (create-effect :tx (m.astronomical-coordinate-tool/update-query-args-tx db tool query-args))))

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

(defmethod handle-event :astronomical-coordinate-tool/object-clicked
  [props {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate clicked-point meta-key current-tool]} detail
        {:astronomical-coordinate/keys [radius]} astronomical-coordinate
        current-coordinate-id (-> current-tool :astronomical-coordinate-tool/query-result first)
        astro-scene (d/pull db '[* {:astro-scene/coordinate [*]}] (get-in props [:astro-scene :db/id]))
        scene-coordinate (get-in astro-scene [:astro-scene/coordinate])
        matrix (m.object/cal-matrix scene-coordinate)
        clicked-point-in-ac (->
                             (v3/from-seq clicked-point)
                             (v3/normalize)
                             (v3/multiply-scalar radius)
                             (v3/apply-matrix4  matrix))]
    ;; (println "service :astronomical-coordinate-tool/object-clicked" (-> detail :object))
    (when (and
           (= current-coordinate-id (:db/id astronomical-coordinate))
           meta-key)
      (create-effect :tx [{:db/id current-coordinate-id
                           :astronomical-coordinate/current-point (vec clicked-point-in-ac)}]))))
