(ns astronomy.service.astronomical-point-tool
  (:require
   [datascript.core :as d]
   [shu.three.vector3 :as v3]
   [methodology.model.object :as m.object]
   [astronomy.component.mouse :as c.mouse]
   [astronomy.model.astronomical-point :as m.apt]
   [astronomy.model.coordinate :as m.coordinate]
   [astronomy.service.effect :as s.effect :refer [create-effect]]))


;; handle-event version

(defmulti handle-event (fn [props env event] (:event/action event)))

(defmethod handle-event :astronomical-point-tool/log
  [_props _env {:event/keys [detail]}]
  (create-effect :log detail))

(defmethod handle-event :astronomical-point-tool/object-clicked
  [props {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [astronomical-point meta-key current-tool]} detail]
    (case (:tool/current-panel current-tool)
      :delete-panel (when (and meta-key astronomical-point)
                      (create-effect :tx (m.apt/delete-astronomical-point-tx astronomical-point)))
      nil)))


(defmethod handle-event :astronomical-point-tool/mouse-clicked
  [props {:keys [db dom]} {:event/keys [detail]}]
  (let [{:keys [meta-key current-tool]} detail]
    (case (:tool/current-panel current-tool)
      :create-panel (let [mouse-direction (c.mouse/get-mouse-direction-vector3 (:three-instance dom))
                          camera-position (c.mouse/get-camera-position (:three-instance dom))
                          act-1 (d/pull db '[*] [:coordinate/name "赤道天球坐标系"])
                          local-vector3 (v3/add (v3/multiply-scalar mouse-direction (:astronomical-coordinate/radius act-1))
                                                camera-position)
                          astro-scene (d/pull db '[* {:astro-scene/coordinate [*]}] (get-in props [:astro-scene :db/id]))
                          scene-coordinate (get-in astro-scene [:astro-scene/coordinate])
                          system-vector (m.coordinate/to-system-vector scene-coordinate local-vector3)
                          apt-1 (m.apt/astronomical-point system-vector)]
                      (when meta-key
                        (create-effect :tx [apt-1])))
      nil)))