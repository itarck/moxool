(ns methodology.service.camera
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :as async :refer [go >! <! go-loop]]
   [datascript.core :as d]
   [posh.reagent :as p]))



(defn play-camera! [three-atom scene-conn]
  (let [camera (:camera @three-atom)
        camera-data (d/pull @scene-conn '[*] [:camera/name "default"])
        {:camera/keys [position quaternion]} camera-data
        [px py pz] position
        [qx qy qz qw] quaternion]
    (when camera
      (j/call-in camera [:position :set] px py pz)
      (j/call-in camera [:quaternion :set] qx qy qz qw))))

(defn record-camera! [three-atom scene-conn]
  (let [camera (:camera @three-atom)]
    (when camera
      (let [position (->
                      (j/get camera :position)
                      (j/call :toArray)
                      vec)
            quaternion (->
                        (j/get camera :quaternion)
                        (j/call :toArray)
                        vec)]
        (p/transact! scene-conn [{:camera/name "default"
                                  :camera/position position
                                  :camera/quaternion quaternion}])))))


;; service

(defn init-service! [props env]
  (let [{:keys [conn dom-atom scene-atom]} env]
    (go-loop []
      (<! (async/timeout 20))
      (try
        (case (:mode @scene-atom)
          :read-and-write (record-camera! dom-atom conn)
          :read-only (play-camera! dom-atom conn)
          nil)
        (catch js/Object e (.log js/console e)))
      (recur))))

