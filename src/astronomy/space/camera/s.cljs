(ns astronomy.space.camera.s
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
    ;; (println "astronomy.space.camera.s: play camera: " position)
    (when camera
      (j/call-in camera [:position :set] px py pz)
      (j/call-in camera [:quaternion :set] qx qy qz qw))))


(defn record-camera! [three-atom scene-conn]
  (let [camera (:camera @three-atom)
        position-v3 (j/get camera :position)
        quaternion-q (j/get camera :quaternion)]
    (when camera
      (let [position (vec (j/call position-v3 :toArray))
            quaternion (vec (j/call quaternion-q :toArray))]
        (p/transact! scene-conn [{:camera/name "default"
                                  :camera/position position
                                  :camera/quaternion quaternion}])))))


;; service

(defn init-service! [props env]
  (let [{:keys [conn dom-atom meta-atom]} env]
    (when meta-atom
      (println "astronomy.space.camera.s: camera service started")
      (go-loop []
        (<! (async/timeout 20))
        (try
          (case (:mode @meta-atom)
            :read-and-write (record-camera! dom-atom conn)
            :read-only (play-camera! dom-atom conn)
            nil)
          (catch js/Object e (.log js/console e)))
        (recur)))))

