(ns astronomy.model.user.spaceship-camera-control
  (:require
   [applied-science.js-interop :as j]
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [shu.goog.math :as gmath]
   [shu.three.spherical :as sph]))


;; 模型介绍：spaceship-camera-control，缩写scc
;;   目标是控制相机在空间中自由移动和旋转，有个注意力中心target
;;   相机控制工具，tool-object合一，而且全局唯一
;; mode：有两种模式 static-mode、orbit-mode
;; 


(def scc-1
  #:spaceship-camera-control
   {:name "default"
    :mode :surface-control
    :min-distance 100
    :max-distance 10000
    :position [2000 2000 2000]
    :up [0 1 0]
    :center [0 0 0]
    :zoom 1
    :tool/name "spaceship camera tool"
    :tool/chinese-name "相机控制"
    :tool/icon "/image/pirate/cow.jpg"
    :entity/type :spaceship-camera-control})

;; schema

(def schema
  #:spaceship-camera-control
   {:name {:db/unique :db.unique/identity}})

;; tranform

(defn cal-component-props [scc mode]
  (let [{:spaceship-camera-control/keys [up center position
                                         min-distance max-distance zoom]} scc
        common-props {:up up
                      :zoom zoom
                      :azimuthRotateSpeed -0.3
                      :polarRotateSpeed -0.3}]
    (case mode
      :orbit-mode (merge common-props
                         {:target center
                          :position position
                          :minDistance min-distance
                          :maxDistance max-distance})
      :static-mode (merge common-props
                          {:target position
                           :position (mapv (fn [v] (* 1.0001 v)) position)
                           :minDistance 1e-3
                           :maxDistance 1e-3}))))


;; tx

(defn set-mode-tx [scc mode]
  [{:db/id (:db/id scc)
    :spaceship-camera-control/mode mode}])

(defn set-position-tx [scc position]
  [{:db/id (:db/id scc)
    :spaceship-camera-control/position position}])

(defn set-min-distance-tx [scc min-distance]
  [{:db/id (:db/id scc)
    :spaceship-camera-control/min-distance min-distance}])



;; others

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

  (cal-component-props scc-1 :orbit-mode)
  
  ;; 
  )