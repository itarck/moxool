(ns astronomy.scripts.init-conn
  (:require
   [cljs.reader :refer [read-string]]
   [cljs.core.async :refer [go >! <! chan]]
   [cljs-http.client :as http]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [shu.goog.math :as gmath]
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [shu.geometry.angle :as shu.angle]
   [shu.astronomy.celestial-coordinate :as shu.cc]
   [shu.astronomy.light :as shu.light]
   [methodology.model.user.backpack :as m.backpack]
   [methodology.model.core :as mtd-model]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.const :refer [ecliptic-axis ecliptic-quaternion lunar-axis-j2000]]
   [astronomy.model.core :as ast-model]
   [astronomy.objects.ellipse-orbit.m :as m.ellipse-orbit]
   [astronomy.objects.clock.m :as m.clock]
   [astronomy.objects.spin.m :as m.spin]
   [astronomy.objects.star.m :as m.star]
   [astronomy.objects.planet.m :as m.planet]))

(def schema (merge ast-model/schema
                   mtd-model/schema))


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
  #:astro-scene {:coordinate -1003
                 :camera [:camera/name "default"]
                 :clock [:clock/name "default"]
                 :celestial-scale 1
                 :scene/name "solar"
                 :scene/chinese-name "太阳系"
                 :scene/scale 10000
                 :entity/type :scene})

(def atmosphere
  #:atmosphere {:name "default"
                :show? true
                :entity/type :atmosphere})

(def sun
  #:star{:name "sun"
         :chinese-name "太阳"
         :color "red"
         :celestial/radius 2.321606103
         :celestial/radius-string "109.1 地球半径"
         :celestial/spin #:spin {:axis (m.spin/cal-spin-axis 286.13 63.87)
                                 :period 25.05
                                 :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 25.05)}
         :celestial/gltf #:gltf {:url "models/16-solar/Sun_1_1391000.glb"
                                 :scale [0.002 0.002 0.002]
                                 :shadow? false}
         :celestial/clock [:clock/name "default"]
         :object/position [0 0 0]
         :object/quaternion ecliptic-quaternion
         :object/scene [:scene/name "solar"]
         :object/show? true
         :entity/chinese-name "太阳"
         :entity/type :star})

(def mercury
  #:planet
   {:name "mercury"
    :chinese-name "水星"
    :star [:star/name "sun"]
    :celestial/radius 0.008132333
    :celestial/radius-string "0.383 地球半径"
    :celestial/orbit #:ellipse-orbit {:semi-major-axis 193.1642156
                                      :eccentricity 0.205630
                                      :inclination-in-degree 7.005
                                      :longitude-of-the-ascending-node-in-degree 48.331
                                      :argument-of-periapsis-in-degree 29.124
                                      :mean-anomaly-in-degree 0
                                      :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree 87.97)

                                      :orbit/type :ellipse-orbit
                                      :orbit/period 87.97
                                      :orbit/color "white"
                                      :orbit/show? false}
    :celestial/spin #:spin {:axis (m.spin/cal-spin-axis 281.01 61.45)
                            :period 58.646
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 58.646)}
    :celestial/gltf #:gltf {:url "models/16-solar/Mercury_1_4878.glb"
                            :scale [0.002 0.002 0.002]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "水星"
    :entity/type :planet})


(def venus
  #:planet
   {:name "venus"
    :chinese-name "金星"
    :star [:star/name "sun"]
    :celestial/radius 0.020172667
    :celestial/radius-string "0.949 地球半径"
    :celestial/orbit #:ellipse-orbit {:semi-major-axis 360.9430361
                                      :eccentricity 0.0067
                                      :inclination-in-degree 3.39458
                                      :longitude-of-the-ascending-node-in-degree 76.678
                                      :argument-of-periapsis-in-degree 55.186
                                      :mean-anomaly-in-degree 0
                                      :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree 224.7)

                                      :orbit/type :ellipse-orbit
                                      :orbit/period 224.7
                                      :orbit/color "gold"
                                      :orbit/show? false}
    #_#:circle-orbit {:star [:star/name "sun"]
                      :radius 350
                      :color "gold"
                      :show? true
                      :start-position (m.planet/random-position 350 ecliptic-axis)
                      :axis ecliptic-axis
                      :period 224.7
                      :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 224.7)}
    :celestial/spin #:spin {:axis (m.spin/cal-spin-axis 92.76	-67.16)
                            :period 243.0185
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 243.0185)}
    :celestial/gltf #:gltf {:url "models/16-solar/Venus_1_12103.glb"
                            :scale [0.002 0.002 0.002]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "金星"
    :entity/type :planet})

(def earth
  #:planet
   {:name "earth"
    :chinese-name "地球"
    :color "blue"
    :star [:star/name "sun"]
    :celestial/radius 0.021
    :celestial/orbit #:ellipse-orbit{:semi-major-axis 499.0052919
                                     :eccentricity 0.01671022
                                     :inclination-in-degree 0.00005
                                     :longitude-of-the-ascending-node-in-degree -11.26064
                                     :argument-of-periapsis-in-degree 114.20783
                                     :mean-anomaly-in-degree 357.51716
                                     :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree 365.256363004)

                                     :orbit/type :ellipse-orbit
                                     :orbit/period 365.256363004
                                     :orbit/color "green"
                                     :orbit/show? false}
    #_#:circle-orbit {:star [:star/name "sun"]
                      :radius 498.6596333
                      :start-position [0 0 -498.6596333]
                      :axis ecliptic-axis
                      :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 365.2564)

                      :orbit/type :circle-orbit
                      :orbit/period 365.2564
                      :orbit/color "green"
                      :orbit/show? false}
    :celestial/spin #:spin {:axis [0 1 0]
                            :axis-center [-0.3971478906347807 0.9177546256839811 -7.295488395810108E-17]
                            :axis-anglar-velocity (shu.angle/period-to-angular-velocity-in-radians (* 365.25 -25722))
                            :period 0.99726968
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 0.99726968)}
    :celestial/gltf #:gltf {:url "models/16-solar/Earth_1_12756.glb"
                            ;; :url "models/16-solar/EarthClouds_1_12756.glb"
                            :scale [0.002 0.002 0.002]
                            :rotation [0 (gmath/to-radians 102) 0]
                            :shadow? true}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "地球"
    :entity/type :planet})

(def moon
  #:satellite
   {:name "moon"
    :chinese-name "月球"
    :color "green"
    :planet [:planet/name "earth"]
    :celestial/radius 0.00579
    :celestial/orbit #:moon-orbit {:axis (seq lunar-axis-j2000)
                                   :axis-precession-center (seq ecliptic-axis)
                                   :axis-precession-velocity (shu.angle/period-to-angular-velocity-in-degrees -6798)

                                   :epoch-days-j20110615 4183.343103981481

                                   :semi-major-axis 1.352270908
                                   :eccentricity 0.0549
                                   :inclination 5.145

                                   :longitude-of-the-ascending-node-j20110615 -96.47355839952931
                                   :argument-of-periapsis-j20110615 282.31
                                   :mean-anomaly-j20110615 73.9

                                   :angular-velocity (shu.angle/period-to-angular-velocity-in-degrees 27.321661)
                                   :anomaly-angular-velocity (shu.angle/period-to-angular-velocity-in-degrees 27.554549886)
                                   :perigee-angular-velocity-eme2000 (shu.angle/period-to-angular-velocity-in-degrees 3233)
                                   :perigee-angular-velocity-emo2000 (shu.angle/period-to-angular-velocity-in-degrees 2191)
                                   :nodical-angular-velocity (shu.angle/period-to-angular-velocity-in-degrees 27.21222082)
                                   :anomaly-month 27.554549886
                                   :nodical-month 27.21222082

                                   :orbit/type :moon-orbit
                                   :orbit/color "white"
                                   :orbit/show? false
                                   :orbit/period 27.321661}
    
    #_#:circle-orbit {:start-position [-0.7592958587703179 -0.20624249999135819 -1.0108881392377498]
                                     :radius 1.281
                                     :axis [-0.34885989419537267 0.9342903258325582 0.07347354134438353]
                                     :axis-precession-center (seq ecliptic-axis)
                                     :axis-precession-velocity (shu.angle/period-to-angular-velocity-in-radians -6798)
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 27.321661)
                                     :draconitic-angular-velocity (shu.angle/period-to-angular-velocity-in-radians 27.212220815)

                                     :orbit/type :circle-orbit
                                     :orbit/color "white"
                                     :orbit/show? true
                                     :orbit/period 27.321661}
    #_#:circle-orbit {:start-position [0 0 -1.281]
                      :radius 1.281
                      :axis m.circle-orbit/lunar-axis
                      :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 27.321661)

                      :orbit/type :circle-orbit
                      :orbit/color "white"
                      :orbit/show? false
                      :orbit/period 27.321661}

    :celestial/spin #:spin {:axis (m.spin/cal-spin-axis 266.86	65.64)
                            :period 27.321661
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 27.321661)}
    :celestial/gltf #:gltf {:url "models/16-solar/Moon_1_3474.glb"
                            :scale [0.002 0.002 0.002]
                            :rotation [0 (/ Math/PI 2) 0]
                            :shadow? true}
    ;; :celestial/gltf #:gltf {:url "models/14-moon/scene.gltf"
                            ;; :scale [0.01 0.01 0.01]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "月球"
    :entity/type :satellite})


(def mars-spin-axis
  (m.spin/cal-spin-axis 317.68	52.89))

(def mars
  #:planet
   {:name "mars"
    :chinese-name "火星"
    :star [:star/name "sun"]
    :celestial/radius 0.011323333
    :celestial/radius-string "0.533 地球半径"
    :celestial/orbit #:ellipse-orbit {:semi-major-axis 760.3146574
                                      :eccentricity 0.09341233
                                      :inclination-in-degree 1.85061
                                      :longitude-of-the-ascending-node-in-degree 49.57854
                                      :argument-of-periapsis-in-degree 286.502
                                      :mean-anomaly-in-degree 19.3871
                                      :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree 686.93)

                                      :orbit/type :ellipse-orbit
                                      :orbit/period 686.93
                                      :orbit/color "red"
                                      :orbit/show? false}
    #_#:circle-orbit {:star [:star/name "sun"]
                    :radius 760.3147908
                    :color "red"
                    :show? true
                    :start-position (m.planet/random-position 760.3147908 ecliptic-axis)
                    :axis ecliptic-axis
                    :period 686.93
                    :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 686.93)}
    :celestial/spin #:spin {:axis (m.spin/cal-spin-axis 317.68	52.89)
                            :period 1.026
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 1.026)}
    :celestial/gltf #:gltf {:url "models/16-solar/Mars_1_6792.glb"
                            :scale [0.002 0.002 0.002]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "火星"
    :entity/type :planet})

(def phobos
  #:satellite
   {:name "phobos"
    :chinese-name "火卫一"
    :color "green"
    :planet [:planet/name "mars"]
    :celestial/radius 0.000037
    :celestial/orbit #:circle-orbit {:start-position (m.planet/random-position 0.031267 mars-spin-axis)
                                     :radius 0.031267
                                     :axis mars-spin-axis
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 0.319)
                                     
                                     :orbit/type :circle-orbit
                                     :orbit/color "gray"
                                     :orbit/show? false
                                     :orbit/period 0.319}
    :celestial/spin #:spin {:axis mars-spin-axis
                            :period 0.319
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 0.319)}
    :celestial/gltf #:gltf {:url "models/16-solar/Phobos_1_1000.glb"
                            :scale [0.07 0.07 0.07]
                            :shadow? false}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "火卫一"
    :entity/type :satellite})


(def deimos
  #:satellite
   {:name "deimos"
    :chinese-name "火卫二"
    :color "green"
    :planet [:planet/name "mars"]
    :celestial/radius 0.0000207
    :celestial/orbit #:circle-orbit {:start-position (m.planet/random-position 0.078200 mars-spin-axis)
                                     :radius 0.078200
                                     :axis mars-spin-axis
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 1.262)

                                     :orbit/type :circle-orbit
                                     :orbit/show? false
                                     :orbit/color "gray"
                                     :orbit/period 1.262}
    :celestial/spin #:spin {:axis mars-spin-axis
                            :period 1.262
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 1.262)}
    :celestial/gltf #:gltf {:url "models/16-solar/Deimos_1_1000.glb"
                            :scale [0.15 0.15 0.15]
                            :shadow? false}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "火卫二"
    :entity/type :satellite})


(def ceres-orbit-axis
  (m.spin/cal-spin-axis 294 (- 90 23.4 4)))
;; 不严谨

(def ceres-spin-axis
  (m.spin/cal-spin-axis 294.18 66.764))

(def ceres
  #:planet
   {:name "ceres"
    :chinese-name "谷神星"
    :star [:star/name "sun"]
    :celestial/radius 0.001576667
    :celestial/radius-string "0.074 地球半径"
    :celestial/orbit #:ellipse-orbit {:semi-major-axis 1380.988711
                                      :eccentricity 0.075823
                                      :inclination-in-degree 10.593
                                      :longitude-of-the-ascending-node-in-degree 80.3293
                                      :argument-of-periapsis-in-degree 72.522
                                      :mean-anomaly-in-degree 95.9891
                                      :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree 1681.63)

                                      :orbit/type :ellipse-orbit
                                      :orbit/period 1681.63
                                      :orbit/color "gray"
                                      :orbit/show? false}
    #_#:circle-orbit {:star [:star/name "sun"]
                      :color "gray"
                      :show? true
                      :radius 1380.955354
                      :start-position (m.planet/random-position 1380.955354 ceres-orbit-axis)
                      :axis ceres-orbit-axis
                      :period 1681.63
                      :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 1681.63)}
    :celestial/spin #:spin {:axis ceres-spin-axis
                            :period 0.3781
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 0.3781)}
    :celestial/gltf #:gltf {:url "models/16-solar/Ceres_1_1000.glb"
                            :scale [0.002 0.002 0.002]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "谷神星"
    :entity/type :planet})

(def eros
  #:planet
   {:name "eros"
    :chinese-name "爱神星"
    :star [:star/name "sun"]
    :celestial/radius 4.33633E-05
    :celestial/radius-string "0.002 地球半径"
    :celestial/orbit #:ellipse-orbit {:semi-major-axis 727.5489763
                                      :eccentricity 0.223
                                      :inclination-in-degree 10.829
                                      :longitude-of-the-ascending-node-in-degree 304.401
                                      :argument-of-periapsis-in-degree 178.664
                                      :mean-anomaly-in-degree 320.215
                                      :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree 643.219)

                                      :orbit/type :ellipse-orbit
                                      :orbit/period 643.219
                                      :orbit/color "HotPink"
                                      :orbit/show? false}
    :celestial/gltf #:gltf {:url "models/16-solar/Eros_1_10.glb"
                            :scale [0.002 0.002 0.002]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "爱神星"
    :entity/type :planet})

(def jupiter-spin-axis
  (m.spin/cal-spin-axis 268.06 64.5))

(def jupiter
  #:planet
   {:name "jupiter"
    :chinese-name "木星"
    :star [:star/name "sun"]
    :celestial/radius 0.238306667
    :celestial/radius-string "11.2 地球半径"
    :celestial/orbit #:ellipse-orbit {:semi-major-axis 2596.953923
                                      :eccentricity 0.048775
                                      :inclination-in-degree 1.305
                                      :longitude-of-the-ascending-node-in-degree 100.492
                                      :argument-of-periapsis-in-degree 275.066
                                      :mean-anomaly-in-degree 18.818
                                      :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree (* 11.856 365))

                                      :orbit/type :ellipse-orbit
                                      :orbit/period (* 11.856 365)
                                      :orbit/color "BurlyWood"
                                      :orbit/show? false}
    #_#:circle-orbit {:star [:star/name "sun"]
                    :color "BurlyWood"
                    :show? true
                    :radius 2596.503011
                    :start-position (m.planet/random-position 2596.503011 ecliptic-axis)
                    :axis ecliptic-axis
                    :period (* 11.856 365)
                    :angular-velocity (shu.angle/period-to-angular-velocity-in-radians (* 11.856 365))}
    :celestial/spin #:spin {:axis jupiter-spin-axis
                            :period 0.413
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 0.413)}
    :celestial/gltf #:gltf {:url "models/16-solar/Jupiter_1_142984.glb"
                            :scale [0.002 0.002 0.002]
                            :shadow? false}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "木星"
    :entity/type :planet})

(def io
  #:satellite
   {:name "io"
    :chinese-name "木卫一"
    :color "green"
    :planet [:planet/name "jupiter"]
    :celestial/radius 0.006100
    :celestial/orbit #:circle-orbit {:start-position (m.planet/random-position 1.405666667 jupiter-spin-axis)
                                     :radius 1.405666667
                                     :axis jupiter-spin-axis
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 1.769137786)

                                     :orbit/type :circle-orbit
                                     :orbit/period 1.769137786
                                     :orbit/color "gray"
                                     :orbit/show? false}
    :celestial/spin #:spin {:axis jupiter-spin-axis
                            :period 1.769137786
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 1.769137786)}
    :celestial/gltf #:gltf {:url "models/16-solar/Io_1_3643.glb"
                            :scale [0.002 0.002 0.002]
                            :shadow? false}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "木卫一"
    :entity/type :satellite})

(def europa
  #:satellite
   {:name "europa"
    :chinese-name "木卫二"
    :color "green"
    :planet [:planet/name "jupiter"]
    :celestial/radius 0.005203
    :celestial/orbit #:circle-orbit {:start-position (m.planet/random-position 2.236780 jupiter-spin-axis)
                                     :radius 2.236780
                                     :axis jupiter-spin-axis
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 3.551181041)

                                     :orbit/type :circle-orbit
                                     :orbit/period 3.551181041
                                     :orbit/color "gray"
                                     :orbit/show? false}
    :celestial/spin #:spin {:axis jupiter-spin-axis
                            :period 3.551181041
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 3.551181041)}
    :celestial/gltf #:gltf {:url "models/16-solar/Europa_1_3138.glb"
                            :scale [0.002 0.002 0.002]
                            :shadow? false}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "木卫二"
    :entity/type :satellite})

(def ganymede
  #:satellite
   {:name "ganymede"
    :chinese-name "木卫三"
    :color "green"
    :planet [:planet/name "jupiter"]
    :celestial/radius 0.008771
    :celestial/orbit #:circle-orbit {:start-position (m.planet/random-position 3.568040 jupiter-spin-axis)
                                     :radius 3.568040
                                     :axis jupiter-spin-axis
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 7.15455296)

                                     :orbit/type :circle-orbit
                                     :orbit/color "gray"
                                     :orbit/period 7.15455296
                                     :orbit/show? false}
    :celestial/spin #:spin {:axis jupiter-spin-axis
                            :period 7.15455296
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 7.15455296)}
    :celestial/gltf #:gltf {:url "models/16-solar/Ganymede_1_5268.glb"
                            :scale [0.002 0.002 0.002]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "木卫三"
    :entity/type :satellite})

(def callisto
  #:satellite
   {:name "callisto"
    :chinese-name "木卫四"
    :color "green"
    :planet [:planet/name "jupiter"]
    :celestial/radius 0.008034
    :celestial/orbit #:circle-orbit {:start-position (m.planet/random-position 6.275697 jupiter-spin-axis)
                                     :radius 6.275697
                                     :axis jupiter-spin-axis
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 16.6890184)
                                     
                                     :orbit/type :circle-orbit
                                     :orbit/color "gray"
                                     :orbit/show? false
                                     :orbit/period 16.6890184}
    :celestial/spin #:spin {:axis jupiter-spin-axis
                            :period 16.6890184
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 16.6890184)}
    :celestial/gltf #:gltf {:url "models/16-solar/Callisto_1_4821.glb"
                            :scale [0.002 0.002 0.002]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "木卫四"
    :entity/type :satellite})

(def saturn-spin-axis
  (m.spin/cal-spin-axis 40.6	83.54))

(def saturn
  #:planet
   {:name "saturn"
    :chinese-name "土星"
    :star [:star/name "sun"]
    :celestial/radius 0.200893333
    :celestial/radius-string "9.45 地球半径"
    :celestial/orbit #:circle-orbit {:star [:star/name "sun"]
                                     :start-position (m.planet/random-position 4750 ecliptic-axis)
                                     :axis ecliptic-axis
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians (* 29.448 365))
                                     :radius 4750

                                     :orbit/type :circle-orbit
                                     :orbit/color "darkgoldenrod"
                                     :orbit/period (* 29.448 365)
                                     :orbit/show? false}
    :celestial/spin #:spin {:axis saturn-spin-axis
                            :period 0.444
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 0.444)}
    :celestial/gltf #:gltf {:url "models/16-solar/Saturn_1_120536.glb"
                            :scale [0.002 0.002 0.002]
                            :shadow? false}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "土星"
    :entity/type :planet})


(def titan
  #:satellite
   {:name "titan"
    :chinese-name "土卫六"
    :color "green"
    :planet [:planet/name "saturn"]
    :celestial/radius 0.008587
    :celestial/orbit #:circle-orbit {:start-position (m.planet/random-position 4.072900 saturn-spin-axis)
                                     :radius 4.072900
                                     :axis saturn-spin-axis
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 15.945)

                                     :orbit/type :circle-orbit
                                     :orbit/period 15.945
                                     :orbit/color "gray"
                                     :orbit/show? false}
    :celestial/spin #:spin {:axis saturn-spin-axis
                            :period 15.945
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 15.945)}
    :celestial/gltf #:gltf {:url "models/16-solar/Titan_1_5150.glb"
                            :scale [0.002 0.002 0.002]
                            :shadow? false}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "土卫六"
    :entity/type :satellite})

(def uranus
  #:planet
   {:name "uranus"
    :chinese-name "天王星"
    :star [:star/name "sun"]
    :celestial/radius 0.085196667
    :celestial/radius-string "4.01 地球半径"
    :celestial/orbit #:circle-orbit {:star [:star/name "sun"]
                                     :start-position (m.planet/random-position 9569.907333 ecliptic-axis)
                                     :radius 9569.907333
                                     :axis ecliptic-axis
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians (* 84.02 365))

                                     :orbit/type :circle-orbit
                                     :orbit/period (* 84.02 365)
                                     :orbit/color "deepskyblue"
                                     :orbit/show? false}
    :celestial/spin #:spin {:axis (m.spin/cal-spin-axis 257.311 -15.175)
                            :period 0.718
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 0.718)}
    :celestial/gltf #:gltf {:url "models/16-solar/Uranus_1_51118.glb"
                            :scale [0.002 0.002 0.002]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "天王星"
    :entity/type :planet})


(def neptune-spin-axis
  (m.spin/cal-spin-axis 299.36	43.46))

(def neptune
  #:planet
   {:name "neptune"
    :chinese-name "海王星"
    :star [:star/name "sun"]
    :celestial/radius 0.082546667
    :celestial/radius-string "3.88 地球半径"
    :celestial/orbit #:circle-orbit {:star [:star/name "sun"]
                                     :radius 14994.17633
                                     :start-position (m.planet/random-position 14994.17633 ecliptic-axis)
                                     :axis ecliptic-axis
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians (* 164.79 365))

                                     :orbit/type :circle-orbit
                                     :orbit/period (* 164.79 365)
                                     :orbit/color "dodgerblue"
                                     :orbit/show? false}
    :celestial/spin #:spin {:axis neptune-spin-axis
                            :period 0.67125
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 0.67125)}
    :celestial/gltf #:gltf {:url "models/16-solar/Neptune_1_49528.glb"
                            :scale [0.002 0.002 0.002]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "海王星"
    :entity/type :planet})


(def triton-orbit-axis
  (m.spin/cal-spin-axis 119.36 -107))

;; 备注：这个轨道角度不是严谨角度，作为定性参数可以看

(def triton
  #:satellite
   {:name "triton"
    :chinese-name "海卫一"
    :color "green"
    :planet [:planet/name "neptune"]
    :celestial/radius 0.004511
    :celestial/orbit #:circle-orbit {:start-position (m.planet/random-position 1.182530 triton-orbit-axis)
                                     :radius 1.182530
                                     :axis triton-orbit-axis
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 5.876854)

                                     :orbit/type :circle-orbit
                                     :orbit/period 5.876854
                                     :orbit/color "gray"
                                     :orbit/show? false}
    :celestial/spin #:spin {:axis triton-orbit-axis
                            :period 5.876854
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 5.876854)}
    :celestial/gltf #:gltf {:url "models/16-solar/Triton_1_2707.glb"
                            :scale [0.002 0.002 0.002]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "海卫一"
    :entity/type :satellite})


(def pluto-spin-axis
  (m.spin/cal-spin-axis 132.99 -6.16))

(def pluto
  #:planet
   {:name "pluto"
    :chinese-name "冥王星"
    :star [:star/name "sun"]
    :celestial/radius 0.003836667
    :celestial/radius-string "0.18 地球半径"
    :celestial/orbit #:ellipse-orbit {:semi-major-axis 19730.31623
                                      :eccentricity 0.24905
                                      :inclination-in-degree 17.1405
                                      :longitude-of-the-ascending-node-in-degree 110.299
                                      :argument-of-periapsis-in-degree 113.834
                                      :mean-anomaly-in-degree 14.53
                                      :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree (* 247.92 365))

                                      :orbit/type :ellipse-orbit
                                      :orbit/period (* 247.92 365)
                                      :orbit/color "gray"
                                      :orbit/show? false
                                      :orbit/radius 19687.93333}
    #_#:circle-orbit {:star [:star/name "sun"]
                    :color "gray"
                    :radius 19687.93333
                    :start-position (m.planet/random-position 19687.93333 ecliptic-axis)
                    :axis ecliptic-axis
                    :period (* 247.92 365)
                    :angular-velocity (shu.angle/period-to-angular-velocity-in-radians (* 247.92 365))}
    :celestial/spin #:spin {:axis pluto-spin-axis
                            :period 6.387230
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 6.387230)}
    :celestial/gltf #:gltf {:url "models/16-solar/Pluto_1_2374.glb"
                            :scale [0.002 0.002 0.002]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "冥王星"
    :entity/type :planet})


(def charon
  #:satellite
   {:name "charon"
    :chinese-name "卡戎"
    :color "white"
    :planet [:planet/name "pluto"]
    :celestial/radius 0.002020
    :celestial/orbit #:circle-orbit {:start-position (m.planet/random-position 0.065237 pluto-spin-axis)
                                     :radius 0.065237
                                     :axis pluto-spin-axis
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 6.387230)

                                     :orbit/type :circle-orbit
                                     :orbit/period 6.387230
                                     :orbit/color "gray"
                                     :orbit/show? false}
    :celestial/spin #:spin {:axis pluto-spin-axis
                            :period 6.387230
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 6.387230)}
    :celestial/gltf #:gltf {:url "models/16-solar/Charon_1_2.glb"
                            :scale [0.002 0.002 0.002]
                            :shadow? false}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "卡戎"
    :entity/type :satellite})


(def eris
  #:planet
   {:name "eris"
    :chinese-name "阋神星"
    :star [:star/name "sun"]
    :celestial/radius 0.004336333
    :celestial/radius-string "0.20 地球半径"
    :celestial/orbit #:ellipse-orbit {:semi-major-axis 21515.0903
                                      :eccentricity 0.19642
                                      :inclination-in-degree 28.2137
                                      :longitude-of-the-ascending-node-in-degree 122.167
                                      :argument-of-periapsis-in-degree 239.041
                                      :mean-anomaly-in-degree 202.67
                                      :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree 103468)

                                      :orbit/type :ellipse-orbit
                                      :orbit/period 103468
                                      :orbit/color "Coral"
                                      :orbit/show? false}
    :celestial/gltf #:gltf {:url "models/16-solar/Eris_1_2326.glb"
                            :scale [0.002 0.002 0.002]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "阋神星"
    :entity/type :planet})

(def haumea
  #:planet
   {:name "haumea"
    :chinese-name "妊神星"
    :star [:star/name "sun"]
    :celestial/radius 0.00239499
    :celestial/radius-string "0.11 地球半径"
    :celestial/orbit #:ellipse-orbit {:semi-major-axis 33756.68643
                                      :eccentricity 0.44177
                                      :inclination-in-degree 44.187
                                      :longitude-of-the-ascending-node-in-degree 35.8696
                                      :argument-of-periapsis-in-degree 151.4305
                                      :mean-anomaly-in-degree 197.634
                                      :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree 203600)

                                      :orbit/type :ellipse-orbit
                                      :orbit/period 203600
                                      :orbit/color "DarkOrchid"
                                      :orbit/show? false}
    :celestial/spin #:spin {:axis (m.spin/cal-spin-axis 282.6	-13)
                            :period 0.163139208
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 0.163139208)}
    :celestial/gltf #:gltf {:url "models/16-solar/Haumea_1_1000.glb"
                            :scale [0.002 0.002 0.002]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "妊神星"
    :entity/type :planet})

(def halley
  #:planet
   {:name "halley"
    :chinese-name "哈雷彗星"
    :star [:star/name "sun"]
    :celestial/radius 3.66921E-05
    :celestial/radius-string "0.0017 地球半径"
    :celestial/orbit #:ellipse-orbit {:semi-major-axis 8899.251333
                                      :eccentricity 0.96714
                                      :inclination-in-degree 162.26
                                      :longitude-of-the-ascending-node-in-degree 58.42
                                      :argument-of-periapsis-in-degree 111.33
                                      :mean-anomaly-in-degree 100
                                      :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree (* 75.3 365))
                                      :orbit/type :ellipse-orbit
                                      :orbit/period (* 75.3 365)
                                      :orbit/color "magenta"
                                      :orbit/show? false
                                      :orbit/radius 8899.251333}
    ;; :celestial/spin #:spin {:axis pluto-spin-axis
    ;;                         :period 6.387230
    ;;                         :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 6.387230)}
    :celestial/gltf #:gltf {:url "models/16-solar/Bennu_1_1.glb"
                            :scale [0.002 0.002 0.002]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "哈雷彗星"
    :entity/type :planet}
  )

(def astronomical-coordinate-1
  #:astronomical-coordinate {:db/id -1001
                             :entity/type :astronomical-coordinate
                             :object/position [0 0 0]
                             :object/quaternion [0 0 0 1]
                             :object/scene [:scene/name "solar"]
                             :coordinate/name "赤道天球坐标系"
                             :coordinate/type :astronomical-coordinate

                             :astronomical-coordinate/radius 10000
                             :astronomical-coordinate/show-latitude? true
                             :astronomical-coordinate/show-longitude? true
                             :astronomical-coordinate/show-latitude-0? false
                             :astronomical-coordinate/show-ecliptic? true
                             :astronomical-coordinate/show-lunar-orbit? true
                             :astronomical-coordinate/center-candidates [{:db/id [:planet/name "earth"]}
                                                                         {:db/id [:planet/name "sun"]}]
                             :astronomical-coordinate/center-object [:planet/name "earth"]
                             :astronomical-coordinate/quaternion [0 0 0 1]})

(def astronomical-coordinate-2
  #:astronomical-coordinate {:db/id -1002
                             :entity/type :astronomical-coordinate
                             :object/position [0 0 0]
                             :object/quaternion (seq m.const/ecliptic-quaternion)
                             :object/scene [:scene/name "solar"]
                             :coordinate/name "黄道天球坐标系"
                             :coordinate/type :astronomical-coordinate
                             :astronomical-coordinate/show-latitude? false
                             :astronomical-coordinate/show-longitude? false
                             :astronomical-coordinate/radius 10000
                             :astronomical-coordinate/center-candidates [{:db/id [:planet/name "earth"]}
                                                                         {:db/id [:planet/name "sun"]}]
                             :astronomical-coordinate/center-object [:planet/name "earth"]
                             :astronomical-coordinate/quaternion (seq m.const/ecliptic-quaternion)})

(def terrestrial-coordinate-1
  #:terrestrial-coordinate {:db/id -1003
                            :entity/type :terrestrial-coordinate
                            :object/position [0 0 0]
                            :object/quaternion [0 0 0 1]
                            :object/scene [:scene/name "solar"]
                            :coordinate/name "地球坐标系"
                            :coordinate/type :terrestrial-coordinate
                            
                            :terrestrial-coordinate/longitude-0-offset -77.444
                            :terrestrial-coordinate/radius 0.0215
                            :terrestrial-coordinate/show-latitude? true
                            :terrestrial-coordinate/show-longitude? true
                            :terrestrial-coordinate/show-latitude-0? true
                            :terrestrial-coordinate/show-longitude-0? true
                            :terrestrial-coordinate/center-object [:planet/name "earth"]})

(def horizon-coordinate-1
  #:horizon-coordinate{:db/id -1004
                       :entity/type :horizon-coordinate
                       :center-object [:planet/name "earth"]
                       :center-radius 0.021024
                       :radius 0.002
                       :longitude-0-offset -77.444
                       :longitude 31.239444444
                       :latitude 30.009012722341744
                       :show-latitude? true
                       :show-longitude? true
                       :show-horizontal-plane? true
                       :coordinate/name "地平坐标系"
                       :coordinate/type :horizon-coordinate

                       :object/scene [:scene/name "solar"]})


(def horizon-coordinate-2
  #:horizon-coordinate{:entity/type :horizon-coordinate
                       :center-object [:planet/name "earth"]
                       :center-radius 0.021024
                       :radius 0.002
                       :longitude-0-offset -77.444
                       :longitude 0
                       :latitude 51.5
                       :show-latitude? true
                       :show-longitude? true
                       :show-horizontal-plane? true
                       :coordinate/name "地平坐标系2"
                       :coordinate/type :horizon-coordinate

                       :object/scene [:scene/name "solar"]})

(def constellation-families 
  [#:constellation-family {:chinese-name "黄道", :color "orange"}
   #:constellation-family {:chinese-name "英仙", :color "red"}
   #:constellation-family {:chinese-name "武仙", :color "green"}
   #:constellation-family {:chinese-name "大熊", :color "CornflowerBlue"}
   #:constellation-family {:chinese-name "猎户", :color "white"}
   #:constellation-family {:chinese-name "幻之水", :color "Aqua"}
   #:constellation-family {:chinese-name "拜耳", :color "yellow"}
   #:constellation-family {:chinese-name "拉卡伊", :color "HotPink"}])

;; * 银心：在天球赤道座标系统的座标是：
;; 赤经 17h45m40.04s，赤纬 -29º 00' 28.1"（J2000 分点）。25000光年

;; * 北银极：换算成2000.0历元的坐标，北银极位于赤经12h 51m 26.282s，赤纬+27° 07′ 42.01″（2000.0历元），银经0度的位置角是122.932°.[4]

(def galaxy-center-vector (shu.cc/cal-position (shu.cc/celestial-coordinate
                                                (shu.angle/convert-hours-to-degrees (shu.angle/gen-hours {:hour 17 :minute 45 :second 40.04}))
                                                (shu.angle/gen-degrees {:degree -29 :minute 0 :second -28.1})
                                                (* shu.light/light-year-unit 25000))))
galaxy-center-vector
;; => #object[Vector3 [-688150222274.2922 -382317840299.07935 -43091769201.20502]]

(def galaxy-north-vector
  (shu.cc/to-unit-vector (shu.cc/celestial-coordinate
                          (shu.angle/convert-hours-to-degrees (shu.angle/gen-hours {:hour 12 :minute 51 :second 26.282}))
                          (shu.angle/gen-degrees {:degree 27 :minute 07 :second  42.01}))))

galaxy-north-vector
;; => #object[Vector3 [-0.19807664997748894 0.45598511375759465 -0.8676653829473483]]

(def galaxy-quaternion (q/from-unit-vectors (v3/vector3 0 1 0) galaxy-north-vector))

galaxy-quaternion
;; => #object[Quaternion [-0.5084623562343109 0 0.11607530061927626 0.8532247985606123]]


(def galaxy
  #:galaxy
   {:name "milky way"
    :chinese-name "银河"
    :radius (* 150000 365 86400)
    :celestial/gltf #:gltf{:url "models/13-galaxy/scene.gltf"
                           :scale (-> (v3/from-seq [0.01 0.005 0.01])
                                      (v3/multiply-scalar 0.3)
                                      seq)
                           :position (-> (v3/from-seq [-1.12 -0.57 1.12])
                                         (v3/multiply-scalar 0.3)
                                         seq)}
    :object/position (vec galaxy-center-vector)
    :object/quaternion (vec galaxy-quaternion)
    :object/scene [:scene/name "solar"]
    :object/show? true
    :entity/chinese-name "银河"
    :entity/type :galaxy})


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

(def universe-tool-1
  #:universe-tool{:astro-scene [:scene/name "solar"]
                  :tool/name "universe tool"
                  :tool/chinese-name "宇宙"
                  :tool/icon "/image/moxool/universe.webp"

                  :entity/type :universe-tool})

(def clock-tool1
  #:clock-tool {:db/id -2
                :status :stop
                :steps-per-second 100
                :step-interval :hour
                :days-per-step (/ 1 24)
                :clock [:clock/name "default"]
                :tool/name "clock control 1"
                :tool/chinese-name "时光机"
                :tool/icon "/image/moxool/clock.jpg"
                :entity/type :clock-tool})


(def info-tool
  #:info-tool {:tool/name "info tool 1"
               :tool/chinese-name "信息查询"
               :tool/icon "/image/moxool/info-tool.jpg"
               :entity/type :info-tool})


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


(def ppt-0-3
  #:ppt {:pages [#:ppt-page{:image-url "/slides/0.3.tidal-locking/Slide1.jpeg"}
                 #:ppt-page{:image-url "/slides/0.3.tidal-locking/Slide2.jpeg"}
                 #:ppt-page{:image-url "/slides/0.3.tidal-locking/Slide3.jpeg"}]
         :chinese-name "0.3.潮汐锁定"
         :current-page 0})

(def ppt-tool
  #:ppt-tool {:query-type :ppt-by-name
              :query-args ["18.食季"]
              :ppts [#:ppt{:pages [#:ppt-page{:image-url "/slides/1.universe-distance/Slide1.jpeg"}
                                   #:ppt-page{:image-url "/slides/1.universe-distance/Slide2.jpeg"}
                                   #:ppt-page{:image-url "/slides/1.universe-distance/Slide3.jpeg"}
                                   #:ppt-page{:image-url "/slides/1.universe-distance/Slide4.jpeg"}
                                   #:ppt-page{:image-url "/slides/1.universe-distance/Slide5.jpeg"}
                                   #:ppt-page{:image-url "/slides/1.universe-distance/Slide6.jpeg"}
                                   #:ppt-page{:image-url "/slides/1.universe-distance/Slide7.jpeg"}
                                   #:ppt-page{:image-url "/slides/1.universe-distance/Slide8.jpeg"}]
                           :chinese-name "1.天文尺度"
                           :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/2.history/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/2.history/Slide2.jpeg"}]
                            :chinese-name "2.天文学的历史"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/3.one-day/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/3.one-day/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/3.one-day/Slide3.jpeg"}]
                            :chinese-name "3.假如你连续看一整天的天空"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/5.decan/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/5.decan/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/5.decan/Slide3.jpeg"}
                                    #:ppt-page{:image-url "/slides/5.decan/Slide4.jpeg"}
                                    #:ppt-page{:image-url "/slides/5.decan/Slide5.jpeg"}]
                            :chinese-name "5.旬星&黄道十二宫"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/6.sirius/Slide01.jpeg"}
                                    #:ppt-page{:image-url "/slides/6.sirius/Slide02.jpeg"}
                                    #:ppt-page{:image-url "/slides/6.sirius/Slide03.jpeg"}
                                    #:ppt-page{:image-url "/slides/6.sirius/Slide04.jpeg"}
                                    #:ppt-page{:image-url "/slides/6.sirius/Slide05.jpeg"}
                                    #:ppt-page{:image-url "/slides/6.sirius/Slide06.jpeg"}
                                    #:ppt-page{:image-url "/slides/6.sirius/Slide07.jpeg"}]
                            :chinese-name "6.古埃及人如何看出一年有365.25天"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/7.moon/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/7.moon/Slide2.jpeg"}]
                            :chinese-name "7.假如你连续看一整月的月亮"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/9.metonic/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/9.metonic/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/9.metonic/Slide3.jpeg"}]
                            :chinese-name "9.默冬章：同步阳历和阴历"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/10.sphere/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/10.sphere/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/10.sphere/Slide3.jpeg"}
                                    #:ppt-page{:image-url "/slides/10.sphere/Slide4.jpeg"}]
                            :chinese-name "10.地球是球形"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/11.heliocentric/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/11.heliocentric/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/11.heliocentric/Slide3.jpeg"}
                                    #:ppt-page{:image-url "/slides/11.heliocentric/Slide4.jpeg"}
                                    #:ppt-page{:image-url "/slides/11.heliocentric/Slide5.jpeg"}
                                    #:ppt-page{:image-url "/slides/11.heliocentric/Slide6.jpeg"}]
                            :chinese-name "11.公元前的日心说模型"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/12.earth-size/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/12.earth-size/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/12.earth-size/Slide3.jpeg"}
                                    #:ppt-page{:image-url "/slides/12.earth-size/Slide4.jpeg"}]
                            :chinese-name "12.地球的大小"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/13.armillary/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/13.armillary/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/13.armillary/Slide3.jpeg"}]
                            :chinese-name "13.浑仪：在地球不同地点看太阳"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/14.equatorial/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/14.equatorial/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/14.equatorial/Slide3.jpeg"}]
                            :chinese-name "14.天球坐标系"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/15.constellation/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/15.constellation/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/15.constellation/Slide3.jpeg"}
                                    #:ppt-page{:image-url "/slides/15.constellation/Slide4.jpeg"}
                                    #:ppt-page{:image-url "/slides/15.constellation/Slide5.jpeg"}
                                    #:ppt-page{:image-url "/slides/15.constellation/Slide6.jpeg"}]
                            :chinese-name "15.星座"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/18.eclipse-season/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/18.eclipse-season/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/18.eclipse-season/Slide3.jpeg"}
                                    #:ppt-page{:image-url "/slides/18.eclipse-season/Slide4.jpeg"}]
                            :chinese-name "18.食季"
                            :current-page 0}]
              :tool/name "ppt tool"
              :tool/chinese-name "脚本"
              :tool/icon "/image/moxool/ppt.jpg"
              :entity/type :ppt-tool})


(def goto-tool-1
  #:goto-celestial-tool {:tool/name "goto celestial tool"
                         :tool/chinese-name "到达星球"
                         :tool/icon "/image/moxool/goto.jpg"
                         :tool/target {:planet/name "earth"}
                         :entity/type :goto-celestial-tool})


(def constellation-tool-1
  #:constellation-tool{:query-type :all
                       :query-args []

                       :tool/name "constellation-tool"
                       :tool/chinese-name "星座"
                       :tool/icon "/image/moxool/constellation.jpg"

                       :entity/type :constellation-tool})

(def atmosphere-tool-1
  #:atmosphere-tool{:tool/target [:atmosphere/name "default"]
                    :tool/name "atmosphere-tool"
                    :tool/chinese-name "大气层工具"
                    :tool/icon "/image/moxool/atmosphere.jpg"

                    :entity/type :atmosphere-tool})

(def eagle-eye-tool
  #:eagle-eye-tool{:tool/target [:spaceship-camera-control/name "default"]
                   :tool/name "eagle-eye-tool"
                   :tool/chinese-name "鹰眼"
                   :tool/icon "/image/moxool/eagle-eye.jpg"

                   :entity/type :eagle-eye-tool})

(def horizon-coordinate-tool
  #:horizon-coordinate-tool {:query-type :one-by-name
                             :query-args-candidates []
                             :query-args []
                             :query-result []

                             :tool/name "horizon-coordinate-tool"
                             :tool/chinese-name "地平坐标系工具"
                             :tool/icon "/image/moxool/horizon-coordinate.jpg"
                             :tool/type :horizon-coordinate-tool
                             :entity/type :horizon-coordinate-tool})

(def astronomical-coordinate-tool
  #:astronomical-coordinate-tool {:query-type :one-by-name
                                  :query-args-candidates []
                                  :query-args []
                                  :query-result []

                                  :tool/name "astronomical-coordinate-tool"
                                  :tool/chinese-name "天球坐标系工具"
                                  :tool/icon "/image/moxool/astronomical-coordinate.jpg"
                                  :tool/type :astronomical-coordinate-tool
                                  :entity/type :astronomical-coordinate-tool})

(def terrestrial-coordinate-tool-1
  #:terrestrial-coordinate-tool {:query-type :one-by-name
                                 :query-args-candidates []
                                 :query-args []
                                 :query-result []

                                 :tool/name "terrestrial-coordinate-tool"
                                 :tool/chinese-name "地球坐标系工具"
                                 :tool/icon "/image/moxool/terrestrial-coordinate.jpg"
                                 :tool/type :terrestrial-coordinate-tool
                                 :entity/type :terrestrial-coordinate-tool})

;; processes


(defn kick-start! [conn]
  (let [clock-id [:clock/name "default"]
        time-in-days 0
        astro-scene (d/pull @conn '[*] [:scene/name "solar"])]
    (p/transact! conn (m.clock/set-clock-time-in-days-tx clock-id time-in-days))
    (p/transact! conn (m.astro-scene/refresh-tx @conn astro-scene))))


(defn init-conn! []
  (let [conn (d/create-conn schema)]
    (d/transact! conn [camera clock scene
                       sun
                      ;;  mercury venus 
                       earth moon
                      ;;  mars 
                      ;;  phobos deimos 
                      ;;  ceres eros 
                      ;;  jupiter 
                      ;;  io europa ganymede callisto
                      ;;  saturn 
                      ;;  titan uranus neptune triton pluto charon 
                      ;;  eris haumea halley
                       galaxy atmosphere
                       astronomical-coordinate-1 astronomical-coordinate-2 terrestrial-coordinate-1
                       horizon-coordinate-1 horizon-coordinate-2])
    (d/transact! conn constellation-families)
    (d/transact! conn [spaceship-camera-control person1 universe-tool-1 clock-tool1 info-tool
                       ppt-tool goto-tool-1 constellation-tool-1 atmosphere-tool-1 eagle-eye-tool
                       horizon-coordinate-tool astronomical-coordinate-tool terrestrial-coordinate-tool-1])

    (let [person (d/pull @conn '[*] [:person/name "dr who"])
          bp (d/pull @conn '[*] (-> person :person/backpack :db/id))]
      (d/transact! conn (m.backpack/put-in-cell-tx bp 0 {:db/id [:tool/name "ppt tool"]}))
      (d/transact! conn (m.backpack/put-in-cell-tx bp 1 {:db/id [:tool/name "clock control 1"]}))
      (d/transact! conn (m.backpack/put-in-cell-tx bp 2 {:db/id [:tool/name "goto celestial tool"]}))
      (d/transact! conn (m.backpack/put-in-cell-tx bp 3 {:db/id [:tool/name "spaceship camera tool"]}))
      (d/transact! conn (m.backpack/put-in-cell-tx bp 4 {:db/id [:tool/name "horizon-coordinate-tool"]}))
      (d/transact! conn (m.backpack/put-in-cell-tx bp 5 {:db/id [:tool/name "terrestrial-coordinate-tool"]}))
      (d/transact! conn (m.backpack/put-in-cell-tx bp 6 {:db/id [:tool/name "constellation-tool"]}))
      (d/transact! conn (m.backpack/put-in-cell-tx bp 7 {:db/id [:tool/name "universe tool"]}))
      (d/transact! conn (m.backpack/put-in-cell-tx bp 8 {:db/id [:tool/name "atmosphere-tool"]}))
      (d/transact! conn (m.backpack/put-in-cell-tx bp 9 {:db/id [:tool/name "eagle-eye-tool"]}))
      (d/transact! conn (m.backpack/put-in-cell-tx bp 11 {:db/id [:tool/name "astronomical-coordinate-tool"]}))
      ;; (d/transact! conn (m.backpack/put-in-cell-tx bp 8 {:db/id [:tool/name "info tool 1"]}))
      )

    (kick-start! conn)
    conn))


(defn async-prepare! []
  (let [ch (chan)
        local-ref (atom {})]
    (go
      (let [response (<! (http/get "/edn/stars.edn"))]
        (swap! local-ref assoc :stars (read-string (:body response))))
      (let [response (<! (http/get "/edn/constellation1.edn"))
            constel1 (read-string (:body response))]
        (swap! local-ref assoc :constellations1 constel1))
      (let [response (<! (http/get "/edn/constellation2.edn"))]
        (swap! local-ref assoc :constellations2 (read-string (:body response))))
      (>! ch @local-ref))
    ch))


(defn load-stars! [conn stars]
  (let [tx (mapv m.star/parse-raw-bsc-data stars)]
    (d/transact! conn tx)))

(defn parse-star-line [conn HR-line]
  (mapv (fn [HR] (:db/id (d/pull @conn '[:db/id] [:star/HR HR]))) HR-line))

(defn parse-constellation [conn constellation]
  (let [star-lines (vec
                    (for [line (:constellation/star-HR-lines constellation)]
                      (parse-star-line conn line)))
        abbreviation (:constellation/abbreviation constellation)]
    #:constellation {:abbreviation abbreviation
                     :star-lines star-lines
                    ;;  :object/scene (:object/scene constellation)
                     }))

(defn load-constellations1! [conn constellations]
  (let [tx (mapv (fn [constel] (parse-constellation conn constel)) constellations)]
    (d/transact! conn tx)))

(defn load-constellations2! [conn constellations]
  (d/transact! conn constellations))

(defn load-dataset! [conn dataset]
  (let [{:keys [stars constellations1 constellations2]} dataset]
    (load-stars! conn stars)
    (load-constellations1! conn constellations1)
    (load-constellations2! conn constellations2)))


(defn async-run! []
  (let [ch (chan)
        conn (init-conn!)]
    (println "async-run init-conn !!!!")
    (go
      (let [dataset (<! (async-prepare!))]
        (load-dataset! conn dataset))
      (let [db-name "/temp/free-mode.edn"
            response (<! (http/post "/api/db/save" {:edn-params {:db-name db-name
                                                                 :db-value (dt/write-transit-str @conn)}}))]
        (println (:body response)))
      (>! ch @conn))
    ch))


(async-run!)