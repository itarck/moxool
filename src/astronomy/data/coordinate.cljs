(ns astronomy.data.coordinate
  (:require
   [shu.astronomy.light :as shu.light]
   [astronomy.model.const :as m.const]))


(def astronomical-coordinate-1
  (merge {:db/id -1001
          :entity/type :astronomical-coordinate
          :object/position [0 0 0]
          :object/quaternion [0 0 0 1]
          :object/scene [:scene/name "solar"]
          :coordinate/name "赤道天球坐标系"
          :coordinate/type :astronomical-coordinate
          :astro-scene/_coordinate [:scene/name "solar"]}
         #:astronomical-coordinate{:radius shu.light/light-year-unit
                                   :show-latitude? true
                                   :show-longitude? true
                                   :show-latitude-0? false
                                   :show-lunar-orbit? false
                                   :center-candidates [{:db/id [:planet/name "earth"]}
                                                       {:db/id [:planet/name "sun"]}]
                                   :center-object [:planet/name "earth"]
                                   :quaternion [0 0 0 1]
                                   :default-color "#770000"
                                   :highlight-color "red"}))


(def astronomical-coordinate-2
  (merge {:db/id -1002
          :entity/type :astronomical-coordinate
          :object/position [0 0 0]
          :object/quaternion (seq m.const/ecliptic-quaternion)
          :object/scene [:scene/name "solar"]
          :coordinate/name "黄道天球坐标系"
          :coordinate/type :astronomical-coordinate}
         #:astronomical-coordinate {:show-latitude? false
                                    :show-longitude? false
                                    :radius shu.light/light-year-unit
                                    :center-candidates [{:db/id [:planet/name "earth"]}
                                                        {:db/id [:planet/name "sun"]}]
                                    :center-object [:planet/name "earth"]
                                    :quaternion (seq m.const/ecliptic-quaternion)
                                    :default-color "#885500"
                                    :highlight-color "orange"}))


(def terrestrial-coordinate-1
  (merge {:db/id -1003
          :entity/type :terrestrial-coordinate
          :object/position [0 0 0]
          :object/quaternion [0 0 0 1]
          :object/scene [:scene/name "solar"]
          :coordinate/name "地球坐标系"
          :coordinate/type :terrestrial-coordinate}
         #:terrestrial-coordinate{:longitude-0-offset -77.444
                                  :radius 0.0215
                                  :show-latitude? false
                                  :show-longitude? false
                                  :show-latitude-0? false
                                  :show-longitude-0? false
                                  :center-object [:planet/name "earth"]
                                  :default-color "#172E83"
                                  :highlight-color "#6989FE"}))


(def horizon-coordinate-1
  #:horizon-coordinate{:db/id -1004
                       :entity/type :horizon-coordinate
                       :center-object [:planet/name "earth"]
                       :center-radius (* 0.021022607702247035 1.0003)
                       :radius 0.001
                       :longitude-0-offset -77.64
                       :longitude 30.910060054274055
                       :latitude 29.878937704799164
                       :show-latitude? false
                       :show-longitude? false
                       :show-horizontal-plane? false
                       :show-compass? false
                       :coordinate/name "开罗地平坐标系"
                       :coordinate/type :horizon-coordinate

                       :object/scene [:scene/name "solar"]})


(def horizon-coordinate-2
  #:horizon-coordinate{:entity/type :horizon-coordinate
                       :center-object [:planet/name "earth"]
                       :center-radius (* 0.020982 1.0003)
                       :radius 0.001
                       :longitude-0-offset -77.64
                       :longitude 0
                       :latitude 51.25
                       :show-latitude? false
                       :show-longitude? false
                       :show-horizontal-plane? false
                       :coordinate/name "伦敦地平坐标系"
                       :coordinate/type :horizon-coordinate

                       :object/scene [:scene/name "solar"]})


(def horizon-coordinate-3
  #:horizon-coordinate{:entity/type :horizon-coordinate
                       :center-object [:satellite/name "moon"]
                       :center-radius (* 0.00579 1.01)
                       :radius (* 0.00579 0.05)
                       :longitude-0-offset 0
                       :longitude 0
                       :latitude 0
                       :show-latitude? false
                       :show-longitude? false
                       :show-horizontal-plane? false
                       :coordinate/name "月球表面"
                       :coordinate/type :horizon-coordinate

                       :object/scene [:scene/name "solar"]})


(def horizon-coordinate-4
  #:horizon-coordinate{:entity/type :horizon-coordinate
                       :center-object [:satellite/name "europa"]
                       :center-radius (* 0.005203 1.03)
                       :radius (* 0.005203 0.05)
                       :longitude-0-offset 0
                       :longitude 90
                       :latitude 30
                       :show-latitude? false
                       :show-longitude? false
                       :show-horizontal-plane? false
                       :coordinate/name "木卫二表面"
                       :coordinate/type :horizon-coordinate

                       :object/scene [:scene/name "solar"]})
;; entry

(def dataset1 [astronomical-coordinate-1 astronomical-coordinate-2 terrestrial-coordinate-1
               horizon-coordinate-1 horizon-coordinate-2 ])

(def dataset2 [horizon-coordinate-3 horizon-coordinate-4])