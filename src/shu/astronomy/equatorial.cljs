(ns shu.astronomy.equatorial
  (:require
   [shu.goog.math :as gmath]
   [shu.three.spherical :as sph]
   [shu.three.vector3 :as v3]))


(def hour-degree (/ 360 24.))
(def minute-degree (/ 1 60.0))
(def second-degree (/ 1 3600.0))

(defn to-right-ascension
  "in degree"
  [hours minutes seconds]
  (+ (* hours hour-degree)
     (* minutes minute-degree)
     (* seconds second-degree)))

(defn to-declination
  "in degree"
  [degrees minutes seconds]
  (* (gmath/sign degrees) (+ (Math/abs degrees)
                             (* minutes minute-degree)
                             (* seconds second-degree))))


(def light-second-unit 1)
(def light-hour-unit (* 3600 light-second-unit))
(def light-day-unit (* light-hour-unit 24))
(def light-year-unit (* 365 light-day-unit))


(defn to-distance [light-years]
  (* light-year-unit light-years))


(defn to-spherical [distance declination right-ascension]
  (let [radius distance
        phi (gmath/to-radians (- 90 declination))
        theta (gmath/to-radians right-ascension)]
    (sph/spherical radius phi theta)))


(defn cal-position [distance declination right-ascension]
  (let [s (to-spherical distance declination right-ascension)]
    (v3/from-spherical s)))



(comment
  (to-spherical 12 0 0)


  (cal-position 12 0 450)

  (to-declination -16.0 42 47.3)
  ;; 
  )

  