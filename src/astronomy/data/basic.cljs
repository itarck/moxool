(ns astronomy.data.basic
  (:require
   [shu.astronomy.light :as shu.light]))

;; 最基础的数据集，使用 :db/id [-1 -100]


(def max-distance (* 10000 46500000000 shu.light/light-year-unit))

(def camera
  #:camera{:name "default"
           :far (* 1e10 365 86400 100000)
           :near 0.001
           :position [2000 2000 2000]
           :quaternion [0 0 0 1]})


(def clock
  #:clock {:name "default"
           :time-in-days 0})

(def scene
  #:astro-scene {:camera [:camera/name "default"]
                 :clock [:clock/name "default"]
                 :celestial-scale 1
                 :scene/name "solar"
                 :scene/chinese-name "太阳系"
                 :scene/scale 10000
                 :entity/type :scene})

(def spaceship-camera-control
  #:spaceship-camera-control {:db/id -10
                              :name "default"
                              :mode :orbit-mode
                              :min-distance 0
                              :max-distance max-distance
                              :position [2000 2000 2000]
                              :direction [-1 -1 -1]
                              :zoom 1
                              :up [0 1 0]
                              :center [0 0 0]
                              :tool/name "spaceship camera tool"
                              :tool/chinese-name "飞船控制"
                              :tool/icon "/image/moxool/spaceship.jpg"
                              :entity/type :spaceship-camera-control})

(def user1
  #:user {:db/id -1
          :name "dr who"
          :mouse #:mouse{:page-x 0
                         :page-y 0
                         :entity/name "default mouse"}
          :camera-control [:spaceship-camera-control/name "default"]
          :backpack #:backpack {:db/id -3
                                :name "default"
                                :owner -1
                                :cell (vec (for [i (range 12)]
                                             #:backpack-cell{:index i}))}
          :entity/type :user})


(def dataset1 [camera clock scene spaceship-camera-control user1])