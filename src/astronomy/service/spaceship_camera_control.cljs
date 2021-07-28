(ns astronomy.service.spaceship-camera-control
  (:require
   [applied-science.js-interop :as j]
   [datascript.core :as d]
   [cljs.core.async :as a :refer [go >! <! chan timeout]]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   [astronomy.service.effect :as s.effect :refer [create-effect]]
   [posh.reagent :as p]))


(defmulti handle-event (fn [props env event] (:event/action event)))


(defmethod handle-event :spaceship-camera-control/change-mode
  [props env {:event/keys [detail]}]
  (let [{:keys [spaceship-camera-control new-mode position]} detail
        tx [#:spaceship-camera-control{:db/id (:db/id spaceship-camera-control)
                                       :position position
                                       :mode new-mode}]]
    (create-effect :tx tx)))

(defmethod handle-event :spaceship-camera-control/change-zoom
  [props env {:event/keys [detail]}]
  (let [{:keys [spaceship-camera-control zoom position]} detail
        tx [#:spaceship-camera-control{:db/id (:db/id spaceship-camera-control)
                                       :position position
                                       :zoom zoom}]]
    (create-effect :tx tx)))

;; handle-event!

(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :spaceship-camera-control/fly
  [props {:keys [conn] :as env} {:event/keys [detail]}]
  (let [entity @(p/pull conn '[*] (get-in props [:spaceship-camera-control :db/id]))]
    (m.spaceship/fly! entity env)))

(defmethod handle-event! :spaceship-camera-control/reset
  [props {:keys [conn dom-atom] :as env} {:event/keys [detail]}]
  (let [entity @(p/pull conn '[*] (get-in props [:spaceship-camera-control :db/id]))
        camera (:camera @dom-atom)]
    (when camera
      (let [position (->
                      (j/get camera :position)
                      (j/call :toArray)
                      vec)]
        (p/transact! conn [{:db/id (:db/id entity)
                            :spaceship-camera-control/position position}])))))


(defmethod handle-event! :spaceship-camera-control/object-clicked
  [props {:keys [service-chan] :as env} {:event/keys [detail] :as event}]
  ;; (println "spaceship-camera-control/object-clicked: " detail)
  (when (:meta-key detail)
    (let [{:keys [click-point current-tool]} detail]
      (go (>! service-chan #:event{:action :spaceship-camera-control/set-position
                                   :detail {:position click-point
                                            :spaceship-camera-control current-tool}})))))

(defmethod handle-event! :spaceship-camera-control/set-position
  [props {:keys [conn dom-atom service-chan] :as env} {:event/keys [detail] :as event}]
  (let [{:keys [position spaceship-camera-control]} detail
        camera (:camera @dom-atom)
        scc (d/pull @conn '[*] (:db/id spaceship-camera-control))
        direction (vec (m.spaceship/get-camera-direction camera))
        tx (m.spaceship/landing-tx scc position direction)]
    (p/transact! conn tx)
    (go (>! service-chan #:event{:action :horizontal-coordinate-tool/update-default
                                 :detail {:spaceship-camera-control {:db/id (:db/id scc)}}}))))

(defmethod handle-event! :spaceship-camera-control/change-mode
  [props {:keys [conn] :as env} {:event/keys [detail]}]
  (let [{:keys [spaceship-camera-control new-mode position]} detail
        tx [#:spaceship-camera-control{:db/id (:db/id spaceship-camera-control)
                                       :position position
                                       :mode new-mode}]]
    (p/transact! conn tx)))

(defmethod handle-event! :spaceship-camera-control/change-zoom
  [{:keys [spaceship-camera-control] :as props} {:keys [conn dom-atom] :as env} {:event/keys [detail]}]
  (let [{:keys [zoom]} detail
        instance (:spaceship-camera-control @dom-atom)
        camera (:camera @dom-atom)
        direction (m.spaceship/get-camera-direction camera)
        new-position (m.spaceship/get-camera-position instance)
        cc (m.spaceship/load-current-position-and-target-from-instance spaceship-camera-control new-position direction)
        tx [(assoc cc :spaceship-camera-control/zoom zoom)]]
    (p/transact! conn tx)))

(defmethod handle-event! :spaceship-camera-control/log
  [props env event]
  (println props event))


(defn init-service! [props {:keys [conn dom-atom process-chan] :as env}]
  (a/go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))