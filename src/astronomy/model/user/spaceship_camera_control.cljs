(ns astronomy.model.user.spaceship-camera-control
  (:require
   [applied-science.js-interop :as j]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]))


(def spaceship-camera-control-entity
  #:spaceship-camera-control
   {:name "default"
    :mode :surface-control
    :min-distance 10000
    :position [2000 2000 2000]
    :up [0 1 0]
    :target [0 0 0]
    :tool/name "spaceship camera tool"
    :tool/chinese-name "相机控制"
    :tool/icon "/image/pirate/cow.jpg"
    :entity/type :spaceship-camera-control})


(def schema
  #:spaceship-camera-control
   {:name {:db/unique :db.unique/identity}})


(defn cal-target [position direction]
  (v3/add position (v3/multiply-scalar direction 1e-3)))

(defn project-on-sphere-surface [position radians]
  (->
   (v3/normalize position)
   (v3/multiply-scalar (* radians 1.01))))


(defn locate-at-min-distance-tx [spaceship-camera-control position up direction]
  (let [{:spaceship-camera-control/keys [min-distance]} spaceship-camera-control
        pv (project-on-sphere-surface position min-distance)
        nup (v3/normalize up)
        dv (->
            (v3/project-on-plane direction nup)
            (v3/normalize)
            (v3/multiply-scalar 1e-3))
        target (cal-target pv dv)
        cc #:spaceship-camera-control
            {:name "default"
             :mode :surface-control
             :position (vec pv)
             :target (vec target)
             :up (vec nup)}]
    cc))

(defn get-camera-position [camera-control-object]
  (let [position (v3/vector3)]
    (j/call-in camera-control-object [:getPosition] position)
    position))

(defn get-camera-direction [camera-object]
  (let [direction (v3/vector3)]
    (j/call-in camera-object [:getWorldDirection] direction)
    direction))

(defn landing! [spaceship-camera-control {:keys [conn dom-atom]}]
  (let [camera-control-object (:spaceship-camera-control @dom-atom)
        position (get-camera-position camera-control-object)
        cc (locate-at-min-distance-tx spaceship-camera-control position position (v3/vector3 0 1 0))]
    (p/transact! conn [cc])))

(defn random-landing! [spaceship-camera-control {:keys [conn]}]
  (let [v (v3/random)
        cc (locate-at-min-distance-tx spaceship-camera-control v v (v3/vector3 0 1 0))]
    (p/transact! conn [cc])))


(defn fly! [spaceship-camera-control {:keys [conn dom-atom]}]
  (let [{:spaceship-camera-control/keys [min-distance]} spaceship-camera-control
        camera-control-object (:spaceship-camera-control @dom-atom)
        position (get-camera-position camera-control-object)
        new-position (-> (-> (v3/normalize position)
                             (v3/multiply-scalar (* 3 min-distance))))
        cc #:spaceship-camera-control {:name "default"
                                       :mode :orbit-control
                                       :position (vec new-position)
                                       :up [0 1 0]
                                       :target [0 0 0]}]

    (p/transact! conn [cc])))

