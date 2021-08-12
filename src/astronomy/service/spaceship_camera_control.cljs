(ns astronomy.service.spaceship-camera-control
  (:require
   [datascript.core :as d]
   [astronomy.service.effect :as s.effect :refer [create-effect effects]]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   [astronomy.component.camera-controls :as c.camera-controls]))


(defmulti handle-event (fn [props env event] (:event/action event)))


(defmethod handle-event :spaceship-camera-control/change-mode
  [_props _env {:event/keys [detail]}]
  (let [{:keys [spaceship-camera-control new-mode position direction]} detail
        tx1 (m.spaceship/refresh-camera-tx spaceship-camera-control position direction)
        tx2 (m.spaceship/set-mode-tx spaceship-camera-control new-mode)]
    (create-effect :tx (concat tx1 tx2))))

(defmethod handle-event :spaceship-camera-control/change-zoom
  [_props _env {:event/keys [detail]}]
  (let [{:keys [spaceship-camera-control zoom position direction]} detail
        tx1 (m.spaceship/refresh-camera-tx spaceship-camera-control position direction)
        tx2 (m.spaceship/set-zoom-tx spaceship-camera-control zoom)]
    (create-effect :tx (concat tx1 tx2))))

(defmethod handle-event :spaceship-camera-control/object-clicked
  [_props _env {:event/keys [detail] :as event}]
  (when (:meta-key detail)
    (let [{:keys [click-point current-tool]} detail
          tx1 (m.spaceship/set-position-tx current-tool (seq click-point))]
      (create-effect :tx tx1))))

(defmethod handle-event :spaceship-camera-control/check-valid-position
  [_props {:keys [db]} {:event/keys [detail] :as event}]
  (let [{:keys [spaceship-camera-control]} detail
        scc (d/pull db '[*] (:db/id spaceship-camera-control))
        tx (m.spaceship/check-valid-position-tx scc)]
    (effects :tx tx)))


;; 这里依赖了component里的方法，只读
(defmethod handle-event :astro-scene.pub/coordinate-changed
  [_props {:keys [db dom]} {detail :event/detail}]
  (let [{:keys [coordinate]} detail
        scc {:db/id [:spaceship-camera-control/name "default"]}
        position (c.camera-controls/get-camera-position (:spaceship-camera-control dom))
        direction (c.camera-controls/get-camera-direction (:camera dom))
        tx1 (m.spaceship/refresh-camera-tx scc position direction)
        tx2 (m.spaceship/update-min-distance-tx db scc coordinate)]
    (effects :tx (concat tx1 tx2)
             :event #:event {:action :spaceship-camera-control/check-valid-position
                             :detail {:spaceship-camera-control scc}})))
