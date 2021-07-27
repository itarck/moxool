(ns astronomy.data.coordinate
  (:require
   [shu.astronomy.light :as shu.light]
   [astronomy.model.const :as m.const]))


(def astronomical-coordinate-1
  #:astronomical-coordinate {:db/id -1001
                             :entity/type :astronomical-coordinate
                             :object/position [0 0 0]
                             :object/quaternion [0 0 0 1]
                             :object/scene [:scene/name "solar"]
                             :coordinate/name "赤道天球坐标系"
                             :coordinate/type :astronomical-coordinate

                             :astronomical-coordinate/radius shu.light/light-year-unit
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
                             :astronomical-coordinate/radius shu.light/light-year-unit
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

                            :astro-scene/_coordinate [:scene/name "solar"]

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


;; entry

(def dataset1 [astronomical-coordinate-1 astronomical-coordinate-2 terrestrial-coordinate-1
               horizon-coordinate-1 horizon-coordinate-2])

