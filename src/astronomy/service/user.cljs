(ns astronomy.service.user
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout]]
   [methodology.model.user.person :as m.person]
   [methodology.model.user.backpack :as m.backpack]
   [astronomy.component.camera-controls :as c.camera-controls]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :user/click-backpack-cell
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [user cell active-cell backpack]} detail
        tx (if (= (:db/id active-cell) (:db/id cell))
             (concat
              (m.backpack/deactive-cell-tx backpack)
              (m.person/drop-tool-tx user))
             (concat
              (m.backpack/active-cell-tx backpack (:db/id cell))
              (m.person/select-tool-tx user (-> cell :backpack-cell/tool :db/id))))]
    (p/transact! conn tx)))


(defmethod handle-event! :user/object-clicked
  [{:keys [user]} {:keys [conn service-chan]} {:event/keys [detail]}]
  (let [person (m.person/pull2 @conn (:db/id user))
        current-tool (:person/right-tool person)
        event #:event {:action (keyword (:entity/type current-tool) :object-clicked)
                       :detail (assoc detail :current-tool current-tool)}]
    ;; (println "in service :user/object-clicked" event)
    (go (>! service-chan event))))


(defmethod handle-event! :user/mouse-clicked
  [{:keys [user]} {:keys [conn service-chan]} {:event/keys [detail]}]
  (let [person (m.person/pull2 @conn (:db/id user))
        current-tool (:person/right-tool person)
        event #:event {:action (keyword (:entity/type current-tool) :mouse-clicked)
                       :detail (assoc detail :current-tool current-tool)}]
    (println "in service :user/mouse-clicked")
    (go (>! service-chan event))))

(defmethod handle-event! :user/mouse-wheeled
  [{:keys [user]} {:keys [conn service-chan dom-atom]} {:event/keys [detail]}]
  (let [person (m.person/pull2 @conn (:db/id user))
        spaceship-camera-control (d/pull @conn '[*] (get-in person [:person/camera-control :db/id])) 
        {:keys [delta]} detail
        {:spaceship-camera-control/keys [mode zoom position]} spaceship-camera-control]
    (when (= :static-mode mode)
      (let [new-zoom (+ zoom (/ delta 100))]
        (when (and (>= new-zoom 1) (<= new-zoom 5))
          (let [direction (c.camera-controls/get-camera-direction (:camera @dom-atom))
                event #:event {:action :spaceship-camera-control/change-zoom
                               :detail {:spaceship-camera-control spaceship-camera-control
                                        :position (vec position)
                                        :direction (vec direction)
                                        :zoom new-zoom}}]
            (go (>! service-chan event))))))))


(defn init-service! [props {:keys [process-chan meta-atom] :as env}]
  (println "user service started")
  (go-loop []
    (let [event (<! process-chan)
          mode (if meta-atom (:mode @meta-atom) :read-and-write)]
      (when (= mode :read-and-write)
        (try
          (handle-event! props env event)
          (catch js/Error e
            (js/console.log e)))))
    (recur)))