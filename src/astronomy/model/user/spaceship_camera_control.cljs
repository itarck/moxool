(ns astronomy.model.user.spaceship-camera-control
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]
   [shu.three.vector3 :as v3]
   [astronomy.model.astronomical-coordinate :as m.astronomical-coordinate]
   [astronomy.model.terrestrial-coordinate :as m.terrestrial-coordinate]
   [astronomy.model.horizon-coordinate :as m.horizon-coordinate]))


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
    :direction [-1 -1 -1]
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
                                         min-distance max-distance zoom direction]} scc
        common-props {:up up
                      :zoom zoom
                      :azimuthRotateSpeed -0.3
                      :polarRotateSpeed -0.3}
        direction-v3 (v3/from-seq direction)]
    (case mode
      :orbit-mode (merge common-props
                         {:target center
                          :position position
                          :minDistance min-distance
                          :maxDistance max-distance})
      :static-mode (merge common-props
                          {:target center
                           :position (vec (v3/add (v3/from-seq center) (v3/multiply-scalar direction-v3 -1e-5)))
                           :minDistance 1e-3
                           :maxDistance 1e-3}))))


;; tx

(defn set-mode-tx [scc mode center]
  {:pre [(s/assert :methodology/entity scc)]}
  [{:db/id (:db/id scc)
    :spaceship-camera-control/mode mode
    :spaceship-camera-control/center center}])

(defn set-position-tx [scc position]
  {:pre [(s/assert :methodology/entity scc)]}
  [{:db/id (:db/id scc)
    :spaceship-camera-control/position position}])

(defn set-min-distance-tx [scc min-distance]
  {:pre [(s/assert :methodology/entity scc)]}
  [{:db/id (:db/id scc)
    :spaceship-camera-control/min-distance min-distance}])

(defn refresh-camera-tx [scc position direction]
  {:pre [(s/assert :methodology/entity scc)]}
  [{:db/id (:db/id scc)
    :spaceship-camera-control/position position
    :spaceship-camera-control/direction direction}])

(defn set-zoom-tx [scc zoom]
  {:pre [(s/assert :methodology/entity scc)]}
  [{:db/id (:db/id scc)
    :spaceship-camera-control/zoom zoom}])

(defn update-min-distance-tx [db scc coordinate]
  (let [coor-1 (d/pull db '[*] (:db/id coordinate))
        min-distance (case (:coordinate/type coor-1)
                       :terrestrial-coordinate (m.terrestrial-coordinate/cal-min-distance db coor-1)
                       :astronomical-coordinate (m.astronomical-coordinate/cal-min-distance db coor-1)
                       0)]
    (set-min-distance-tx scc min-distance)))

(defn check-valid-position-tx [scc]
  (let [{:spaceship-camera-control/keys [min-distance position]} scc
        position-v3 (v3/from-seq position)
        length (v3/length position-v3)
        new-position (if (< length min-distance)
                       (seq (v3/multiply-scalar (v3/normalize position-v3)
                                                (* min-distance 2)))
                       position)]
    [{:db/id (:db/id scc)
      :spaceship-camera-control/position new-position}]))