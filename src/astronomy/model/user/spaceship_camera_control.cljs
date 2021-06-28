(ns astronomy.model.user.spaceship-camera-control
  (:require
   [applied-science.js-interop :as j]
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [shu.goog.math :as gmath]
   [shu.three.spherical :as sph]))


(def sample
  #:spaceship-camera-control
   {:name "default"
    :mode :surface-control
    :min-distance 10000
    :surface-ratio 1.002
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


(defn surface-mode? [scc]
  (= (:spaceship-camera-control/mode scc) :surface-control))


(defn cal-longitude [scc]
  (let [[px py pz] (:spaceship-camera-control/position scc)
        [r phi theta] (vec (sph/from-cartesian-coords px py pz))]
    (->
     (+ theta Math/PI)
     (gmath/standard-angle-in-radians)
     (gmath/to-degree))))


(defn get-landing-position-in-scene [scc astro-scene]
  (let [scale (:scene/scale astro-scene)
        landing-position (:spaceship-camera-control/landing-position scc)]
    (mapv #(* 1.00005 (/ % scale)) landing-position)))

(defn cal-up-quaternion [scc]
  (seq (q/from-unit-vectors
        (v3/vector3 0 1 0)
        (v3/normalize (v3/from-seq (:spaceship-camera-control/up scc))))))

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


(defn locate-at-position-tx [position up direction]
  (let [pv position
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

(defn load-current-position-and-target-from-instance [scc v-position v-direction]
  (let [pv v-position
        dv (->
            (v3/normalize v-direction)
            (v3/multiply-scalar 1e-3))
        target (cal-target pv dv)
        cc #:spaceship-camera-control
            {:db/id (:db/id scc)
             :mode :surface-control
             :position (vec pv)
             :target (vec target)}]
    cc))

(defn reset-position-tx [scc position]
  [{:db/id (:db/id scc)
    :spaceship-camera-control/position position}])

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


(defn change-to-static-mode! [{:keys [conn dom-atom]}]
  (let [camera-control-object (:spaceship-camera-control @dom-atom)
        camera (:camera @dom-atom)
        position (get-camera-position camera-control-object)
        direction (get-camera-direction camera)
        up (v3/vector3 0 1 0)
        target (cal-target position direction)
        cc [#:spaceship-camera-control
             {:name "default"
              :mode :static-control
              :position (vec position)
              :target (vec target)
              :up (vec up)}]]
    (p/transact! conn cc)))


(defn cal-position [landing-position surface-ratio]
  (mapv #(* surface-ratio %) landing-position))


(defn landing-tx [scc landing-position direction]
  (let [{:spaceship-camera-control/keys [surface-ratio]} scc
        position (cal-position landing-position surface-ratio)
        target (vec (cal-target (v3/from-seq position) (v3/from-seq direction)))
        tx [#:spaceship-camera-control {:db/id (:db/id scc)
                                        :mode :surface-control
                                        :landing-position landing-position
                                        :direction direction
                                        :position position
                                        :target (vec target)
                                        :up (vec (v3/normalize (v3/from-seq landing-position)))}]]
    tx))

(defn change-surface-ratio-tx [scc surface-ratio direction]
  (let [{:spaceship-camera-control/keys [landing-position]} scc
        position (cal-position landing-position surface-ratio)
        target (vec (cal-target (v3/from-seq position) (v3/from-seq direction)))]
    [#:spaceship-camera-control {:db/id (:db/id scc)
                                 :position position
                                 :target target
                                 :surface-ratio surface-ratio}]))


(comment 
  
  (cal-longitude #:spaceship-camera-control{:position [-1 0 0]})


  )