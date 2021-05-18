(ns shu.astronomy.test-equatorial
  (:require
   [shu.three.vector3 :as v3]
   [shu.three.euler :as e]
   [shu.three.quaternion :as q]
   [shu.astronomy.equatorial :as eq]))


;; * 银心：在天球赤道座标系统的座标是：
;; 赤经 17h45m40.04s，赤纬 -29º 00' 28.1"（J2000 分点）。25000光年

;; * 北银极：换算成2000.0历元的坐标，北银极位于赤经12h 51m 26.282s，赤纬+27° 07′ 42.01″（2000.0历元），银经0度的位置角是122.932°.[4]


(def galaxy-center [(eq/to-distance 25000)
                    (eq/to-declination -29 00 28.1)
                    (eq/to-right-ascension 17 45 40.04)])

(def galaxy-north [1
                   (eq/to-declination 27 07 42.01)
                   (eq/to-right-ascension 12 51 26.282)])

galaxy-center
;; => [788400000000 -28.992194444444443 255.7611222222222]

(v3/normalize (apply eq/cal-position galaxy-center))
;; => #object[Vector3 [-0.8476862047773847 -0.4849287675026375 -0.21510971311981011]]

(eq/cal-position 1 0 0)


(def galxy-north-vector
  (v3/normalize (apply eq/cal-position galaxy-north)))

galxy-north-vector
;; => #object[Vector3 [-0.013316133076086615 0.45598511375759465 -0.8898877775491545]]


(q/from-unit-vectors (v3/vector3 0 1 0) galxy-north-vector)
;; => #object[Quaternion [-0.5214849469040238 0 0.0078034142341800745 0.8532247985606123]]

(def earth-north [1
                  (eq/to-declination 66 33 38.84)
                  (eq/to-right-ascension 18 0 0)])

(def earth-north-vector 
  (v3/normalize (apply eq/cal-position earth-north)))

earth-north-vector
  ;; => #object[Vector3 [-0.3977758748849012 0.9174826174699723 -7.307024279337483e-17]]

(->>
 (q/from-unit-vectors (v3/vector3 0 1 0) earth-north-vector)
 (e/from-quaternion))
;; => #object[Euler [-7.307024279337482e-17 -1.5158197258412427e-17 0.4090914079589274 "XYZ"]]

(q/from-unit-vectors (v3/vector3 0 1 0) earth-north-vector)
;; => #object[Quaternion [-3.7312971357182504e-17 0 0.20312235540435689 0.9791533632352931]]
