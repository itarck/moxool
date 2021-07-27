(ns astronomy.data.basic)


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
  #:spaceship-camera-control
   {:name "default"
    :mode :orbit-control
    :surface-ratio 1.0001
    :min-distance 210
    :position [2000 2000 2000]
    :zoom 1
    :up [0 1 0]
    :target [0 0 0]
    :tool/name "spaceship camera tool"
    :tool/chinese-name "飞船控制"
    :tool/icon "/image/moxool/spaceship.jpg"
    :entity/type :spaceship-camera-control})

(def person1
  #:person {:db/id -1
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
            :entity/type :person})


(def dataset1 [camera clock scene spaceship-camera-control person1])