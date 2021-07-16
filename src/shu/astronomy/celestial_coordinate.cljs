(ns shu.astronomy.celestial-coordinate
  (:require
   [shu.geometry.angle :as angle]
   [shu.three.spherical :as sph]
   [shu.three.vector3 :as v3]
   [shu.arithmetic.number :as number]))

;; 说明
;; celestial-coordinate，天球坐标是一种复合结构，有两个分量，longitude 经度和 latitude 纬度都用度数表示
;; 0经度的方向是Z轴正方向

(comment
  (def sample
    #:celestial-coordinate {:longitude 30
                            :latitude 40
                            :radius 1})
  ;; 
  )

;; 创建

(defn celestial-coordinate
  ([longitude latitude]
   #:celestial-coordinate {:longitude longitude
                           :latitude latitude
                           :radius 1})
  ([longitude latitude radius]
   #:celestial-coordinate {:longitude longitude
                           :latitude latitude
                           :radius radius}))

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

(defn to-vector [celestial-coordinate]
  (let [{:celestial-coordinate/keys [longitude latitude radius]} celestial-coordinate
        phi (angle/to-radians (- 90 latitude))
        theta (angle/to-radians longitude)]
    (v3/from-spherical-coords radius phi theta)))

(defn to-unit-vector [celestial-coordinate]
  (v3/normalize (to-vector celestial-coordinate)))

(defn from-unit-vector [unit-vector]
  (let [[x y z] unit-vector
        [_radius phi theta] (sph/from-cartesian-coords x y z)]
    #:celestial-coordinate {:longitude (angle/to-degrees theta)
                            :latitude (- 90 (angle/to-degrees phi))
                            :radius 1}))

(defn from-vector [vector]
  (let [[x y z] vector
        [radius phi theta] (sph/from-cartesian-coords x y z)]
    #:celestial-coordinate {:longitude (angle/to-degrees theta)
                            :latitude (- 90 (angle/to-degrees phi))
                            :radius radius}))

;; 计算位置

(defn cal-position [celestial-coordinate]
  (let [{:celestial-coordinate/keys [longitude latitude radius]} celestial-coordinate
        phi (angle/to-radians (- 90 latitude))
        theta (angle/to-radians longitude)]
    (v3/from-spherical-coords radius phi theta)))


(comment

  (right-ascension sample)
  (declination sample)
  (from-unit-vector (to-unit-vector sample))

  (v3/almost-equal?
   (v3/normalize (cal-position (celestial-coordinate 30 60 100)))
   (to-unit-vector (celestial-coordinate 30 60)))


  ;; 
  )