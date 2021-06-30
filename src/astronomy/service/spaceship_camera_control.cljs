(ns astronomy.service.spaceship-camera-control
  (:require
   [applied-science.js-interop :as j]
   [datascript.core :as d]
   [cljs.core.async :as a :refer [go >! <! chan timeout]]
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   [posh.reagent :as p]))


(defn init-keyboard-listener! [props {:keys [service-chan] :as env}]
  (j/call js/document
          :addEventListener "keydown"
          (fn [e]
            (let [keydown (j/get e :key)]
              (cond
                (= "w" keydown) (go (>! service-chan #:event {:action :spaceship-camera-control/up}))

                (= "s" keydown) (go (>! service-chan #:event {:action :spaceship-camera-control/down}))

                (= "a" keydown) (go (>! service-chan #:event {:action :spaceship-camera-control/left}))

                (= "d" keydown) (go (>! service-chan #:event {:action :spaceship-camera-control/right}))

                (= "z" keydown) (go (>! service-chan #:event {:action :spaceship-camera-control/random-zoom}))
                :else nil)))))


(defmulti handle-event! (fn [props env event] (:event/action event)))

(defmethod handle-event! :spaceship-camera-control/up
  [props {:keys [conn dom-atom]} {:event/keys [detail]}]
  (let [instance (:spaceship-camera-control @dom-atom)
        camera (:camera @dom-atom)
        direction (v3/multiply-scalar (m.spaceship/get-camera-direction camera) 0.03)
        new-position (v3/add (m.spaceship/get-camera-position instance) direction)
        cc (m.spaceship/locate-at-position-tx new-position new-position direction)]
    (p/transact! conn [cc])))

(defmethod handle-event! :spaceship-camera-control/down
  [props {:keys [conn dom-atom]} {:event/keys [detail]}]
  (let [instance (:spaceship-camera-control @dom-atom)
        camera (:camera @dom-atom)
        direction (v3/multiply-scalar (m.spaceship/get-camera-direction camera) 0.03)
        new-position (v3/sub (m.spaceship/get-camera-position instance) direction)
        cc (m.spaceship/locate-at-position-tx new-position new-position direction)]
    (p/transact! conn [cc])))


(defmethod handle-event! :spaceship-camera-control/left
  [props {:keys [conn dom-atom]} {:event/keys [detail]}]
  (let [instance (:spaceship-camera-control @dom-atom)
        camera (:camera @dom-atom)
        direction (m.spaceship/get-camera-direction camera)
        step (-> (v3/cross (m.spaceship/get-camera-position instance) direction)
                 (v3/normalize)
                 (v3/multiply-scalar 0.03))
        new-position (v3/add (m.spaceship/get-camera-position instance) step)
        cc (m.spaceship/locate-at-position-tx new-position new-position direction)]
    (p/transact! conn [cc])))

(defmethod handle-event! :spaceship-camera-control/right
  [props {:keys [conn dom-atom]} {:event/keys [detail]}]
  (let [instance (:spaceship-camera-control @dom-atom)
        camera (:camera @dom-atom)
        direction (m.spaceship/get-camera-direction camera)
        step (-> (v3/cross (m.spaceship/get-camera-position instance) direction)
                 (v3/normalize)
                 (v3/multiply-scalar 0.03))
        new-position (v3/sub (m.spaceship/get-camera-position instance) step)
        cc (m.spaceship/locate-at-position-tx new-position new-position direction)]
    (p/transact! conn [cc])))

(defmethod handle-event! :spaceship-camera-control/landing
  [props {:keys [conn] :as env} {:event/keys [detail]}]
  (let [entity @(p/pull conn '[*] (get-in props [:spaceship-camera-control :db/id]))]
    (m.spaceship/landing! entity env)))


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
  [props {:keys [conn dom-atom service-chan] :as env} {:event/keys [detail] :as event}]
  (println "spaceship-camera-control/object-clicked: " detail)
  (when (:meta-key detail)
    (let [{:keys [click-point current-tool]} detail]
      (go (>! service-chan #:event{:action :spaceship-camera-control/landing-at-position
                                   :detail {:position click-point
                                            :spaceship-camera-control current-tool}})))))

(defmethod handle-event! :spaceship-camera-control/landing-at-position
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
  [props {:keys [conn service-chan] :as env} {:event/keys [detail]}]
  (let [{:keys [new-mode]} detail]
    (case (str new-mode)
      "surface-control" (go (>! service-chan #:event {:action :spaceship-camera-control/landing}))
      "orbit-control" (go (>! service-chan #:event {:action :spaceship-camera-control/fly}))
      "static-control"  (m.spaceship/change-to-static-mode! env)
      (println "not match " new-mode))))


(defmethod handle-event! :spaceship-camera-control/change-surface-ratio
  [props {:keys [conn dom-atom] :as env} {:event/keys [detail]}]
  (let [{:keys [surface-ratio spaceship-camera-control-id]} detail
        camera (:camera @dom-atom)
        scc (d/pull @conn '[*] spaceship-camera-control-id)
        direction (m.spaceship/get-camera-direction camera)
        tx (m.spaceship/change-surface-ratio-tx scc surface-ratio direction) ]
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
  (init-keyboard-listener! props env)
  (a/go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))



