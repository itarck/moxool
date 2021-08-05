(ns astronomy.service.astronomical-point-tool
  (:require
   [astronomy.model.astronomical-point :as m.apt]
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
  [{:keys [astro-scene] :as props} {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [meta-key current-tool mouse-direction]} detail]
    (case (:tool/current-panel current-tool)
      :create-panel (let [scene-coordinate (m.astro-scene/pull-scene-coordinate db astro-scene)
                          camera (m.astro-scene/pull-scene-camera db astro-scene)
                          apt-1 (m.apt/from-local-camera-view scene-coordinate
                                                              (:camera/positon camera) mouse-direction)]
                      (when meta-key
                        (create-effect :tx [apt-1])))
      nil)))

(defmethod handle-event :astronomical-point-tool/keyboard-down
  [{:keys [astro-scene] :as props} {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [meta-key key current-tool]} detail]
    (println "astronomical-point-tool/keyboard-down: " key)))