(ns astronomy.tools.astronomical-point-tool.h
  (:require
   [astronomy.objects.astro-scene.m :as m.astro-scene]
   [astronomy.objects.astronomical-point.m :as m.apt]
   [astronomy.tools.astronomical-point-tool.m :as m.apt-tool]
   [astronomy.service.effect :as s.effect :refer [create-effect effects]]))


;; handle-event version

(defmulti handle-event (fn [props env event] (:event/action event)))

(defmethod handle-event :astronomical-point-tool/log
  [_props _env {:event/keys [detail]}]
  (create-effect :log detail))

(defmethod handle-event :astronomical-point-tool/object-clicked
  [props {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [astronomical-point meta-key current-tool]} detail]
    (case (:tool/current-panel current-tool)
      :pull-panel (create-effect :tx (m.apt-tool/pull-point-tx current-tool astronomical-point))
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
                        (effects :tx [apt-1]
                                 :event #:event {:action :astronomical-point-tool/pull-latest-point
                                                 :detail {:current-tool current-tool}})))
      nil)))

(defmethod handle-event :astronomical-point-tool/pull-latest-point
  [_prop {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [current-tool]} detail
        tx (m.apt-tool/pull-lastest-point-tx db current-tool)]
    (create-effect :tx tx)))

(defmethod handle-event :astronomical-point-tool/keyboard-down
  [{:keys [astro-scene] :as props} {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [meta-key key current-tool]} detail]
    (println "astronomical-point-tool/keyboard-down: " key)))


(defmethod handle-event :astronomical-point-tool/change-size
  [{:keys [astro-scene] :as props} {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [astronomical-point size]} detail]
    (create-effect :tx (m.apt/set-size-tx astronomical-point size))))