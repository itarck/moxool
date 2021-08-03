(ns astronomy.service.astronomical-point-tool
  (:require
   [datascript.core :as d]
   [shu.three.vector3 :as v3]
   [methodology.model.object :as m.object]
   [astronomy.service.effect :as s.effect :refer [create-effect]]))


;; handle-event version

(defmulti handle-event (fn [props env event] (:event/action event)))

(defmethod handle-event :astronomical-point-tool/log
  [_props _env {:event/keys [detail]}]
  (create-effect :log detail))

(defmethod handle-event :astronomical-point-tool/object-clicked
  [props {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [clicked-point meta-key current-tool]} detail
        astronomical-coordinate (d/pull db '[:astronomical-coordinate/radius] [:coordinate/name "赤道天球坐标系"])
        {:astronomical-coordinate/keys [radius]} astronomical-coordinate
        astro-scene (d/pull db '[* {:astro-scene/coordinate [*]}] (get-in props [:astro-scene :db/id]))
        scene-coordinate (get-in astro-scene [:astro-scene/coordinate])
        matrix (m.object/cal-matrix scene-coordinate)
        clicked-point-in-ac (->
                             (v3/from-seq clicked-point)
                             (v3/normalize)
                             (v3/multiply-scalar radius)
                             (v3/apply-matrix4  matrix))
        tx [{:db/id (:db/id current-tool)
             :astronomical-point-tool/current-point (vec clicked-point-in-ac)}]]
    (when meta-key
      (create-effect :tx tx))))