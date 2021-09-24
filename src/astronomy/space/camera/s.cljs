(ns astronomy.space.camera.s
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :as async :refer [chan go >! <! go-loop]]
   [datascript.core :as d]
   [posh.reagent :as p]))



(defn play-camera! [three-atom scene-conn]
  (let [camera (:camera @three-atom)
        camera-data (d/pull @scene-conn '[*] [:camera/name "default"])
        {:camera/keys [position quaternion]} camera-data
        [px py pz] position
        [qx qy qz qw] quaternion]
    ;; (println "[play] astronomy.space.camera.s, play camera:  " position)
    ;; (js/console.log "[play] astronomy.space.camera.s, play camera before:  " (j/get-in camera [:position]))
    (when camera
      (j/call-in camera [:position :set] px py pz)
      (j/call-in camera [:quaternion :set] qx qy qz qw))
    ;; (js/console.log "[play] astronomy.space.camera.s, play camera after:  " (j/get-in camera [:position]))
    ))


(defn record-camera! [three-atom scene-conn]
  (let [camera (:camera @three-atom)
        position-v3 (j/get camera :position)
        quaternion-q (j/get camera :quaternion)]
    (when camera
      (let [position (vec (j/call position-v3 :toArray))
            quaternion (vec (j/call quaternion-q :toArray))]
        ;; (println "[record] astronomy.space.camera.s!!!!: " position)
        (p/transact! scene-conn [{:camera/name "default"
                                  :camera/position position
                                  :camera/quaternion quaternion}])))))


;; service

(defn init-service! [props env]
  (let [{:keys [conn dom-atom meta-atom]} env
        signal-chan (chan)]
    (when meta-atom
      (println "astronomy.space.camera.s: camera service started")
      (go-loop []
        (let [[v p] (async/alts! [(async/timeout 20) signal-chan])]
          (if (= p signal-chan)
            (do
              (println "camera service killed")
              :killed)
            (do
              (try
                (case (:mode @meta-atom)
                  :read-and-write (record-camera! dom-atom conn)
                  :read-only (play-camera! dom-atom conn)
                  nil)
                (catch js/Object e (.log js/console e)))
              (recur)))))
      signal-chan)))

