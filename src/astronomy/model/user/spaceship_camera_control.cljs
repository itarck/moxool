(ns astronomy.model.user.spaceship-camera-control
  (:require
   [shu.three.vector3 :as v3]))


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
        position-v3 (v3/from-seq position)
        direction-v3 (v3/from-seq direction)]
    (case mode
      :orbit-mode (merge common-props
                         {:target center
                          :position position
                          :minDistance min-distance
                          :maxDistance max-distance})
      :static-mode (merge common-props
                          {:target position 
                           :position (vec (v3/add position-v3 (v3/multiply-scalar direction-v3 -1e-3)))
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

(defn refresh-camera-tx [scc position direction]
  [{:db/id (:db/id scc)
    :spaceship-camera-control/position position
    :spaceship-camera-control/direction direction}])

(defn set-zoom-tx [scc zoom]
  [{:db/id (:db/id scc)
    :spaceship-camera-control/zoom zoom}])