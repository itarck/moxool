(ns astronomy.service.spaceship-camera-control
  (:require
   [applied-science.js-interop :as j]
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
                :else nil)))))


(defn init-service! [props {:keys [conn dom-atom process-chan] :as env}]
  (init-keyboard-listener! props env)
  (a/go-loop []
    (println "spaceship camera service started")
    (let [event (<! process-chan)
          entity @(p/pull conn '[*] [:spaceship-camera-control/name "default"])
          instance (:spaceship-camera-control @dom-atom)
          camera (:camera @dom-atom)]
      (println "spaceship camera control service event: " event)
      (println "spaceship camera control service dom-atom: " dom-atom)
      (case (:event/action event)
        :spaceship-camera-control/up (let [direction (v3/multiply-scalar (m.spaceship/get-camera-direction camera) 0.1)
                                           new-position (v3/add (m.spaceship/get-camera-position instance) direction)
                                           cc (m.spaceship/locate-at-min-distance-tx entity new-position new-position direction)]
                                       (p/transact! conn [cc]))
        :spaceship-camera-control/down (let [direction (v3/multiply-scalar (m.spaceship/get-camera-direction camera) 0.1)
                                             new-position (v3/sub (m.spaceship/get-camera-position instance) direction)
                                             cc (m.spaceship/locate-at-min-distance-tx entity new-position new-position direction)]
                                         (p/transact! conn [cc]))
        :spaceship-camera-control/left (let [direction (m.spaceship/get-camera-direction camera)
                                             step (->
                                                   (v3/cross (m.spaceship/get-camera-position instance) direction)
                                                   (v3/normalize)
                                                   (v3/multiply-scalar 0.1))
                                             new-position (v3/add (m.spaceship/get-camera-position instance) step)
                                             cc (m.spaceship/locate-at-min-distance-tx entity new-position new-position direction)]
                                         (p/transact! conn [cc]))
        :spaceship-camera-control/right (let [direction (m.spaceship/get-camera-direction camera)
                                              step (->
                                                    (v3/cross (m.spaceship/get-camera-position instance) direction)
                                                    (v3/normalize)
                                                    (v3/multiply-scalar 0.1))
                                              new-position (v3/sub (m.spaceship/get-camera-position instance) step)
                                              cc (m.spaceship/locate-at-min-distance-tx entity new-position new-position direction)]
                                          (p/transact! conn [cc]))
        :spaceship-camera-control/landing (m.spaceship/landing! entity env)
        :spaceship-camera-control/fly (m.spaceship/fly! entity env)
        :spaceship-camera-control/reset (let [camera (:camera @dom-atom)]
                                          ;; (js/console.log "spaceship-camera-control/reset: " camera)
                                          (when camera
                                            (let [position (->
                                                            (j/get camera :position)
                                                            (j/call :toArray)
                                                            vec)]
                                              (p/transact! conn [{:db/id (:db/id entity)
                                                                  :spaceship-camera-control/position position}]))))

        (println "not find")))
    (recur)))



