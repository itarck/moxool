(ns shu.astronomy.celestial-coordinate
  (:require
   [shu.geometry.angle :as angle]
   [shu.three.spherical :as sph]
   [shu.three.vector3 :as v3]
   [shu.arithmetic.number :as number]))

;; 说明
;; celestial-coordinate，天球坐标是一种复合结构，有两个分量，longitude 经度和 latitude 纬度都用度数表示

(comment
  (def sample
    #:celestial-coordinate {:longitude 30
                            :latitude 40})
  ;; 
  )

;; 创建

(defn celestial-coordinate [longitude latitude]
  #:celestial-coordinate {:longitude longitude
                          :latitude latitude})

;; 获取分量

(defn right-ascension
  "in hour format"
  [celestial-coordinate]
  (let [{:celestial-coordinate/keys [longitude]} celestial-coordinate]
    (angle/convert-degrees-to-hours longitude)))

(defn declination
  "in degrees"
  [celestial-coordinate]
  (:celestial-coordinate/latitude celestial-coordinate))

;; 比较

(defn almost-equal? [cc1 cc2]
  (and
   (number/almost-equal? (angle/standard-angle-in-degrees (:celestial-coordinate/longitude cc1))
                         (angle/standard-angle-in-degrees (:celestial-coordinate/longitude cc2)))
   (number/almost-equal? (:celestial-coordinate/latitude cc1) 
                         (:celestial-coordinate/latitude cc2))))

;; 和单位向量的转化

(defn to-unit-vector [celestial-coordinate]
  (let [radius 1
        phi (angle/to-radians (- 90 (:celestial-coordinate/latitude celestial-coordinate)))
        theta (angle/to-radians (:celestial-coordinate/longitude celestial-coordinate))]
    (v3/from-spherical-coords radius phi theta)))

(defn from-unit-vector [unit-vector]
  (let [[x y z] unit-vector
        [_radius phi theta] (sph/from-cartesian-coords x y z)]
    #:celestial-coordinate {:longitude (angle/to-degrees theta)
                            :latitude (- 90 (angle/to-degrees phi))}))

;; 计算位置

(defn cal-position [celestial-coordinate distance]
  (let [{:celestial-coordinate/keys [longitude latitude]} celestial-coordinate
        radius distance
        phi (angle/to-radians (- 90 latitude))
        theta (angle/to-radians longitude)]
    (v3/from-spherical-coords radius phi theta)))


(comment

  (right-ascension sample)
  (declination sample)
  (from-unit-vector (to-unit-vector sample))

  (v3/almost-equal?
   (v3/normalize (cal-position (celestial-coordinate 30 60) 2500))
   (to-unit-vector (celestial-coordinate 30 60)))


  ;; 
  )