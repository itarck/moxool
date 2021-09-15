(ns astronomy.data.celestial
  (:require
   [shu.goog.math :as gmath]
   [shu.geometry.angle :as shu.angle]
   [astronomy.lib.const :as m.const :refer [ecliptic-axis ecliptic-quaternion lunar-axis-j2000]]
   [astronomy.objects.ellipse-orbit.m :as m.ellipse-orbit]
   [astronomy.objects.spin.m :as m.spin]
   [astronomy.objects.planet.m :as planet.m]))


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
         :object/quaternion (vec ecliptic-quaternion)
         :object/scene [:scene/name "solar"]
         :object/show? true
         :entity/chinese-name "太阳"
         :entity/type :star})

(def mercury
  #:planet
   {:name "mercury"
    :chinese-name "水星"
    :show-name? false
    :track-position? false
    :show-tracks? true
    :position-log []
    :star [:star/name "sun"]
    :celestial/radius 0.008132333
    :celestial/radius-string "0.383 地球半径"
    :celestial/orbit #:ellipse-orbit {:semi-major-axis 193.1642156
                                      :eccentricity 0.205630
                                      :inclination-in-degree 7.005
                                      :longitude-of-the-ascending-node-in-degree 48.331
                                      :argument-of-periapsis-in-degree 29.124
                                      :mean-anomaly-in-degree 174.796
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
    :show-name? false
    :track-position? false
    :show-tracks? true
    :star [:star/name "sun"]
    :celestial/radius 0.020172667
    :celestial/radius-string "0.949 地球半径"
    :celestial/orbit #:ellipse-orbit {:semi-major-axis 360.9430361
                                      :eccentricity 0.0067
                                      :inclination-in-degree 3.39458
                                      :longitude-of-the-ascending-node-in-degree 76.678
                                      :argument-of-periapsis-in-degree 55.186
                                      :mean-anomaly-in-degree 50.115
                                      :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree 224.7)

                                      :orbit/type :ellipse-orbit
                                      :orbit/period 224.7
                                      :orbit/color "gold"
                                      :orbit/show? false}
    #_#:circle-orbit {:star [:star/name "sun"]
                      :radius 350
                      :color "gold"
                      :show? true
                      :start-position (planet.m/random-position 350 ecliptic-axis)
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
    :celestial/mass 1
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
                                     :orbit/show? false
                                     :orbit/show-helper-lines? false}
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
    :celestial/mass 0.0123
    :celestial/radius 0.00579
    :celestial/orbit #:moon-orbit {:axis (seq lunar-axis-j2000)
                                   :axis-precession-center (vec ecliptic-axis)
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
  (m.spin/cal-spin-axis 317.68 52.89))

(def mars
  #:planet
   {:name "mars"
    :chinese-name "火星"
    :show-name? false
    :track-position? false
    :show-tracks? true
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
                    :start-position (planet.m/random-position 760.3147908 ecliptic-axis)
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
    :celestial/orbit #:circle-orbit {:start-position (planet.m/random-position 0.031267 mars-spin-axis)
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
    :celestial/orbit #:circle-orbit {:start-position (planet.m/random-position 0.078200 mars-spin-axis)
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
                      :start-position (planet.m/random-position 1380.955354 ceres-orbit-axis)
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
    :show-name? false
    :track-position? false
    :show-tracks? true
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
                    :start-position (planet.m/random-position 2596.503011 ecliptic-axis)
                    :axis ecliptic-axis
                    :period (* 11.856 365)
                    :angular-velocity (shu.angle/period-to-angular-velocity-in-radians (* 11.856 365))}
    :celestial/spin #:spin {:axis jupiter-spin-axis
                            :period 0.413
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 0.413)}
    :celestial/gltf #:gltf {:url "models/16-solar/Jupiter_1_142984.glb"
                            :scale [0.002 0.002 0.002]
                            :shadow? true}
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
    :celestial/orbit #:circle-orbit {:start-position (planet.m/random-position 1.405666667 jupiter-spin-axis)
                                     :radius 1.405666667
                                     :axis jupiter-spin-axis
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 1.769137786)

                                     :orbit/type :circle-orbit
                                     :orbit/period 1.769137786
                                     :orbit/color "gray"
                                     :orbit/show? true}
    :celestial/spin #:spin {:axis jupiter-spin-axis
                            :period 1.769137786
                            :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 1.769137786)}
    :celestial/gltf #:gltf {:url "models/16-solar/Io_1_3643.glb"
                            :scale [0.002 0.002 0.002]
                            :shadow? true}
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
    :celestial/orbit #:circle-orbit {:start-position (planet.m/random-position 2.236780 jupiter-spin-axis)
                                     :radius 2.236780
                                     :axis jupiter-spin-axis
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 3.551181041)

                                     :orbit/type :circle-orbit
                                     :orbit/period 3.551181041
                                     :orbit/color "gray"
                                     :orbit/show? true}
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
    :celestial/orbit #:circle-orbit {:start-position (planet.m/random-position 3.568040 jupiter-spin-axis)
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
    :celestial/orbit #:circle-orbit {:start-position (planet.m/random-position 6.275697 jupiter-spin-axis)
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
    :show-name? false
    :track-position? false
    :show-tracks? true
    :star [:star/name "sun"]
    :celestial/radius 0.200893333
    :celestial/radius-string "9.45 地球半径"
    :celestial/orbit #_#:circle-orbit {:star [:star/name "sun"]
                                       :start-position (planet.m/random-position 4750 ecliptic-axis)
                                       :axis (vec ecliptic-axis)
                                       :angular-velocity (shu.angle/period-to-angular-velocity-in-radians (* 29.448 365))
                                       :radius 4750

                                       :orbit/type :circle-orbit
                                       :orbit/color "darkgoldenrod"
                                       :orbit/period (* 29.448 365)
                                       :orbit/show? false}
    #:ellipse-orbit {:semi-major-axis 4781.472421
                     :eccentricity 0.055723219
                     :inclination-in-degree 2.48524
                     :longitude-of-the-ascending-node-in-degree 113.642811
                     :argument-of-periapsis-in-degree 336.013862
                     :mean-anomaly-in-degree 320.346
                     :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree (* 29.448 365))

                     :orbit/type :ellipse-orbit
                     :orbit/period (* 29.448 365)
                     :orbit/color "darkgoldenrod"
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
    :celestial/orbit #:circle-orbit {:start-position (planet.m/random-position 4.072900 saturn-spin-axis)
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
    :celestial/orbit #:ellipse-orbit {:semi-major-axis 9595.568552
                                      :eccentricity 0.044405586
                                      :inclination-in-degree 0.772556
                                      :longitude-of-the-ascending-node-in-degree 73.989821
                                      :argument-of-periapsis-in-degree 96.541318
                                      :mean-anomaly-in-degree 142.955717
                                      :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree (* 84.02 365))
                                      :orbit/type :ellipse-orbit
                                      :orbit/period (* 84.02 365)
                                      :orbit/color "deepskyblue"
                                      :orbit/show? false
                                      :orbit/radius 9569.907333}
    #_#:circle-orbit {:star [:star/name "sun"]
                    :start-position (planet.m/random-position 9569.907333 m.const/ecliptic-axis)
                    :radius 9569.907333
                    :axis (vec ecliptic-axis)
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
    :celestial/orbit #:ellipse-orbit {:semi-major-axis 15021.8711
                                      :eccentricity 0.011214269
                                      :inclination-in-degree 1.767975
                                      :longitude-of-the-ascending-node-in-degree 131.79431
                                      :argument-of-periapsis-in-degree 265.646853
                                      :mean-anomaly-in-degree 267.767281
                                      :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree (* 164.79 365.25))
                                      :orbit/type :ellipse-orbit
                                      :orbit/period (* 164.79 365)
                                      :orbit/color "dodgerblue"
                                      :orbit/show? false
                                      :orbit/radius 14994.17633}
    #_#:circle-orbit {:star [:star/name "sun"]
                      :radius 14994.17633
                      :start-position (planet.m/random-position 14994.17633 ecliptic-axis)
                      :axis (vec ecliptic-axis)
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
    :celestial/orbit #:circle-orbit {:start-position (planet.m/random-position 1.182530 triton-orbit-axis)
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
                    :start-position (planet.m/random-position 19687.93333 ecliptic-axis)
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
    :celestial/orbit #:circle-orbit {:start-position (planet.m/random-position 0.065237 pluto-spin-axis)
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
                                      :mean-anomaly-in-degree 38.38
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


;; dataset3

(def atmosphere
  #:atmosphere {:name "default"
                :show? true
                :object/scene [:scene/name "solar"]
                :entity/type :atmosphere})


(def cg-1
  #:celestial-group
   {:object/position [0 0 100]
    :object/quaternion [0 0 0 1]
    :celestial/clock [:clock/name "default"]
    :celestial/_group [{:db/id [:planet/name "earth"]} {:db/id [:satellite/name "moon"]}]
    :entity/chinese-name "地月系"
    :entity/type :celestial-group})


(def geosynchronous-satellite
  #:satellite
   {:name "geosynchronous-satellite"
    :chinese-name "地球同步卫星"
    :color "gray"
    :planet [:planet/name "earth"]
    :celestial/radius (* 0.00579 0.11)
    :celestial/orbit #:circle-orbit {:start-position [0 0 0.1318]
                                     :radius 0.1318
                                     :axis (seq m.const/ecliptic-axis)
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 0.99726968)
                                     :orbit/type :circle-orbit
                                     :orbit/color "gray"
                                     :orbit/show? false
                                     :orbit/period 0.99726968}
     
    :celestial/gltf #:gltf {:url "models/16-solar/Moon_1_3474.glb"
                            :scale [0.002 0.002 0.002]
                            :rotation [0 (/ Math/PI 2) 0]
                            :shadow? false}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "地球同步卫星"
    :entity/type :satellite})


(def newton-apple
  #:satellite
   {:name "newton-apple"
    :chinese-name "牛顿的苹果"
    :color "red"
    :planet [:planet/name "earth"]
    :celestial/radius (* 0.00579 0.017)
    :celestial/orbit #:circle-orbit {:start-position [0 0 -0.021488]
                                     :radius 0.021488
                                     :axis (seq m.const/ecliptic-axis)
                                     :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 0.059193)
                                     :orbit/type :circle-orbit
                                     :orbit/color "red"
                                     :orbit/show? false
                                     :orbit/period 0.059193}

    :celestial/gltf #:gltf {:url "models/16-solar/Moon_1_3474.glb"
                            :scale [0.002 0.002 0.002]
                            :rotation [0 (/ Math/PI 2) 0]
                            :shadow? false}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :object/show? true
    :entity/chinese-name "牛顿的苹果"
    :entity/type :satellite})

;; datasets

(def dataset1 [sun earth moon])

(def dataset2 [mercury venus
               mars
               phobos deimos
               ceres eros
               jupiter
               io europa ganymede callisto
               saturn
               titan uranus neptune triton pluto charon
               eris haumea halley])

(def dataset3 [atmosphere])

(def dataset4 [mercury venus mars jupiter saturn])

(def dataset-1610 [mercury venus
                   mars
                   jupiter
                   io europa ganymede callisto
                   saturn])

(def dataset5 [cg-1])

(def dataset-newton [geosynchronous-satellite newton-apple])