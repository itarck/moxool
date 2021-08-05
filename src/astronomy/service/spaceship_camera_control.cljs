(ns astronomy.service.spaceship-camera-control
  (:require
   [astronomy.service.effect :as s.effect :refer [create-effect]]
   [astronomy.component.camera-controls :as c.camera-controls]
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

(defmethod handle-event :spaceship-camera-control/mouse-wheeled
  [_props {:keys [dom]} {:event/keys [detail] :as event}]
  (let [{:keys [current-tool delta]} detail
        {:spaceship-camera-control/keys [mode zoom]} current-tool]
    (when (= :static-mode mode)
      (let [new-zoom (+ zoom (/ delta 100))]
        (when (and (>= new-zoom 1) (<= new-zoom 5))
          (let [position (c.camera-controls/get-camera-position (:spaceship-camera-control dom))
                direction (c.camera-controls/get-camera-direction (:camera dom))]
            (create-effect :event #:event {:action :spaceship-camera-control/change-zoom
                                           :detail {:spaceship-camera-control current-tool
                                                    :position (vec position)
                                                    :direction (vec direction)
                                                    :zoom new-zoom}})))))))
