(ns astronomy.model.core
  (:require
   [datascript.core :as d]
   [methodology.model.core :as mtd-model]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.model.circle-orbit :as m.circle-orbit]
   [astronomy.model.clock :as m.clock]
   [astronomy.model.coordinate :as m.coordinate]
   [astronomy.model.galaxy :as m.galaxy]
   [astronomy.model.planet :as m.planet]
   [astronomy.model.satellite :as m.satellite]
   [astronomy.model.spin :as m.spin]
   [astronomy.model.star :as m.star]
   [astronomy.model.constellation :as m.constellation]

   [astronomy.model.user.clock-tool :as m.clock-tool]
   [astronomy.model.user.info-tool :as m.info-tool]
   [astronomy.model.user.coordinate-tool :as m.coordinate-tool]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   ))


(def schema
  (merge
   m.astro-scene/schema
   m.celestial/schema
   m.circle-orbit/schema
   m.clock/schema
   m.coordinate/schema
   m.galaxy/schema
   m.planet/schema
   m.satellite/schema
   m.spin/schema
   m.star/schema
   m.constellation/schema
   
   m.clock-tool/schema
   m.spaceship/schema
   m.info-tool/schema
   m.coordinate-tool/schema))


(def camera
  #:camera{:name "default"
           :far (* 1e10 365 86400 1000)
           :near 0.001
           :position [100 100 20]
           :quaternion [0 0 0 1]})

(def scene
  #:astro-scene {:astro-scene/coordinate -10
                 :scene/name "solar"
                 :scene/chinese-name "太阳系"
                 :scene/scale 100
                 :entity/type :scene})

(def clock
  #:clock {:name "default"
           :time-in-days 0})


(def coordinate-1
  #:coordinate {:db/id -10
                :name "default"
                :clock [:clock/name "default"]
                :position [0 0 0]
                :quaternion [0 0 0 1]})

(def person1
  #:person {:db/id -1
            :name "dr who"
            :backpack #:backpack {:db/id -3
                                  :name "default"
                                  :owner -1
                                  :cell (vec (for [i (range 10)]
                                               #:backpack-cell{:index i}))}
            :entity/type :person})


(def spaceship-camera-control
  #:spaceship-camera-control
   {:name "default"
    :mode :orbit-control
    :min-distance 8
    :position [200 200 200]
    :up [0 1 0]
    :target [0 0 0]
    :tool/name "spaceship camera tool"
    :tool/chinese-name "相机控制"
    :tool/icon "/image/pirate/cow.jpg"
    :entity/type :spaceship-camera-control})


(def basic-db
  (let [conn (d/create-conn (merge mtd-model/schema schema))]
    (d/transact! conn [camera scene clock coordinate-1])
    (d/transact! conn [person1 spaceship-camera-control])
    @conn))