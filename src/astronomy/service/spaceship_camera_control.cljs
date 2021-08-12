(ns astronomy.service.spaceship-camera-control
  (:require
   [astronomy.service.effect :as s.effect :refer [create-effect]]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]))


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

(defmethod handle-event :astro-scene.pub/coordinate-changed 
  [props env event]
  (create-effect :log (str "in spaceship service" event)))
