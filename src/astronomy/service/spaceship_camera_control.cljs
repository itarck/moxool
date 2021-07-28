(ns astronomy.service.spaceship-camera-control
  (:require
   [astronomy.service.effect :as s.effect :refer [create-effect]]))


(defmulti handle-event (fn [props env event] (:event/action event)))


(defmethod handle-event :spaceship-camera-control/change-mode
  [_props _env {:event/keys [detail]}]
  (let [{:keys [spaceship-camera-control new-mode position direction]} detail
        tx [#:spaceship-camera-control{:db/id (:db/id spaceship-camera-control)
                                       :position position
                                       :direction direction
                                       :mode new-mode}]]
    (create-effect :tx tx)))

(defmethod handle-event :spaceship-camera-control/change-zoom
  [_props _env {:event/keys [detail]}]
  (let [{:keys [spaceship-camera-control zoom position direction]} detail
        tx [#:spaceship-camera-control{:db/id (:db/id spaceship-camera-control)
                                       :position position
                                       :direction direction
                                       :zoom zoom}]]
    (create-effect :tx tx)))

(defmethod handle-event :spaceship-camera-control/object-clicked
  [_props _env {:event/keys [detail] :as event}]
  (when (:meta-key detail)
    (let [{:keys [click-point current-tool]} detail
          tx [{:db/id (:db/id current-tool)
               :spaceship-camera-control/position (seq click-point)}]]
      (create-effect :tx tx))))

