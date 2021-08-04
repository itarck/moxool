(ns astronomy.service.astronomical-point-tool
  (:require
   [datascript.core :as d]
   [shu.three.vector3 :as v3]
   [methodology.model.camera :as m.camera]
   [astronomy.model.const :as const]
   [astronomy.model.astronomical-point :as m.apt]
   [astronomy.model.coordinate :as m.coordinate]
   [astronomy.model.astro-scene :as m.astro-scene]
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
  [props {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [meta-key current-tool mouse-direction]} detail
        camera (m.camera/pull-unique-one db)]
    (case (:tool/current-panel current-tool)
      :create-panel (let [local-vector3 (v3/add (v3/multiply-scalar (v3/from-seq mouse-direction) const/astronomical-sphere-radius)
                                                (v3/from-seq (:camera/positon camera)))
                          scene-coordinate (m.astro-scene/pull-scene-coordinate db)
                          system-vector (m.coordinate/to-system-vector scene-coordinate local-vector3)
                          apt-1 (m.apt/from-position system-vector)]
                      (when meta-key
                        (create-effect :tx [apt-1])))
      nil)))

