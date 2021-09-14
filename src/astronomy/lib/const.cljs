(ns astronomy.lib.const
  (:require
   [cljs-time.core :as t]
   [shu.geometry.angle :as shu.angle]
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [shu.astronomy.light :as shu.light]
   [shu.astronomy.celestial-coordinate :as shu.cc]
   [shu.calendar.epoch :as epoch]))

;; 一些天文学中的常数，如果是向量，都在J2000的赤道坐标系里表示

;; 黄赤交角

(def ecliptic-angle 23.439291111)

(def ecliptic-axis
  (-> (shu.cc/celestial-coordinate 270 (- 90 ecliptic-angle))
      (shu.cc/to-unit-vector)))
;; => #object[Vector3 [-0.3977771559301345 0.9174820620699532 -7.307047811756652e-17]]


;; 黄道的朝向，作为太阳的初始位置
(def ecliptic-quaternion
  (q/from-unit-vectors (v3/vector3 0 1 0) ecliptic-axis))
;; => #object[Quaternion [-3.731309692824097e-17 0 0.20312303898136075 0.9791532214290961]]


;; 天球距离

(def astronomical-sphere-radius
  shu.light/light-year-unit)


;; 月球轨道的北极方向

(defn from-ecliptic-to-equatorial [vector-in-ecliptic]
  (v3/apply-axis-angle vector-in-ecliptic (v3/vector3 0 0 1) (shu.angle/to-radians ecliptic-angle)))

(defn from-equatorial-to-ecliptic [vector-in-equatorial]
  (v3/apply-axis-angle vector-in-equatorial (v3/vector3 0 0 1) (shu.angle/to-radians ecliptic-angle)))


(def lunar-axis-j2000
  (v3/apply-axis-angle
   (->
    (shu.cc/celestial-coordinate 192 84.85)
    (shu.cc/to-unit-vector)
    (from-ecliptic-to-equatorial))
   ecliptic-axis
   (* (epoch/to-epoch-days (t/date-time 2010 7 2))
      (shu.angle/period-to-angular-velocity-in-radians 6798))))

;; (v3/from-seq [-0.34885989419537267 0.9342903258325582 0.07347354134438353])