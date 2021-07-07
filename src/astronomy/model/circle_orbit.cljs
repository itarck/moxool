(ns astronomy.model.circle-orbit
  (:require
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.goog.math :as gmath]
   [cljs-time.core :as t]
   [shu.astronomy.date-time :as dt]))

;; 带轴进动的圆形轨道
;; 按照参考点 2010-7-2，黄经 192度，黄纬 84.85度。轴进动的周期是6798天，顺时针方向
;; 这样就可以计算 J2000 的轴位置

;; data

(def schema {})

(def ecliptic-angle 23.439291111)

(defn cal-vector [longitude latitude]
  (v3/from-spherical-coords
   1
   (gmath/to-radians (- 90.0 latitude))
   (gmath/to-radians longitude)))

(defn from-ecliptic-to-equatorial [vector-in-ecliptic]
  (v3/apply-axis-angle vector-in-ecliptic (v3/vector3 0 0 1) (gmath/to-radians ecliptic-angle)))

(defn from-equatorial-to-ecliptic [vector-in-equatorial]
  (v3/apply-axis-angle vector-in-equatorial (v3/vector3 0 0 1) (gmath/to-radians ecliptic-angle)))

(def ecliptic-axis 
  (from-ecliptic-to-equatorial (v3/vector3 0 1 0)))

(defn period-to-angular-velocity [period]
  (/ (* 2 Math/PI) period))

(def date-time-1 (t/date-time 2010 7 2))

(dt/to-epoch-days date-time-1)
;; => 3834.5007428703702


(def lunar-axis-in-date-time-1 (from-ecliptic-to-equatorial (cal-vector 192 84.85)))

(def lunar-axis
  (v3/apply-axis-angle
   (from-ecliptic-to-equatorial (cal-vector 192 84.85))
   ecliptic-axis
   (* (dt/to-epoch-days (t/date-time 2010 7 2))
      (period-to-angular-velocity 6798))))


(def sample1
  #:circle-orbit {:start-position [0 0 -1.281]
                  :radius 1.281
                  :axis [0 1 0]
                  :angular-velocity (period-to-angular-velocity 27)})

(def moon-sample1
  #:circle-orbit {:start-position [-0.016974988456277856 0.09411960646215528 -1.2774248899431686]
                  :radius 1.281
                  :axis (seq lunar-axis)
                  :axis-precession-center (seq ecliptic-axis)
                  :axis-precession-velocity (period-to-angular-velocity -6798)
                  :angular-velocity (period-to-angular-velocity 27.321661)
                  :draconitic-angular-velocity (period-to-angular-velocity 27.212220815)

                  :orbit/type :circle-orbit
                  :orbit/color "white"
                  :orbit/show? false
                  :orbit/period 27.321661})



(defn cal-current-axis [orbit epoch-days]
  (let [{:circle-orbit/keys [axis axis-precession-center axis-precession-velocity]} orbit]
    (if axis-precession-center
      (v3/apply-axis-angle
       (v3/from-seq axis)
       (v3/from-seq axis-precession-center)
       (* epoch-days axis-precession-velocity))
      axis)))

(defn cal-position-1 [orbit days]
  (let [{:circle-orbit/keys [start-position axis angular-velocity]} orbit
        position-angle (* angular-velocity days)
        position (v3/apply-axis-angle
                  (v3/from-seq start-position)
                  (v3/normalize (v3/from-seq axis))
                  position-angle)]
    position))

(defn cal-position-without-axis-precession [orbit epoch-days]
  (let [{:circle-orbit/keys [start-position axis angular-velocity]} orbit
        position-angle (* angular-velocity epoch-days)
        position (v3/apply-axis-angle
                  (v3/from-seq start-position)
                  (v3/normalize (v3/from-seq axis))
                  position-angle)]
    position))

(defn cal-position-with-axis-precession [orbit epoch-days]
  (let [{:circle-orbit/keys [start-position axis axis-precession-center
                             axis-precession-velocity draconitic-angular-velocity]} orbit
        position-angle (* draconitic-angular-velocity epoch-days)
        position (v3/apply-axis-angle
                  (v3/from-seq start-position)
                  (v3/normalize (v3/from-seq axis))
                  position-angle)]
    (v3/apply-axis-angle position
                         (v3/from-seq axis-precession-center)
                         (* epoch-days axis-precession-velocity))))

(defn cal-position [orbit epoch-days]
  (let [{:circle-orbit/keys [axis-precession-center]} orbit]
    (if axis-precession-center
      (cal-position-with-axis-precession orbit epoch-days)
      (cal-position-without-axis-precession orbit epoch-days))))

(defn cal-tilt-quaternion [circle-orbit]
  (q/from-unit-vectors
   (v3/vector3 0 1 0)
   (v3/normalize (v3/from-seq (:circle-orbit/axis circle-orbit)))))


(comment
  (cal-position sample1 365)
  (cal-tilt-quaternion sample1)

  (cal-current-axis moon-sample1 3834.5007428703702)
  ;; => #object[Vector3 [-0.41329422719604925 0.9063546235612364 -0.08780192546629355]]

  lunar-axis-in-date-time-1
;; => #object[Vector3 [-0.4132942271960496 0.9063546235612369 -0.0878019254662936]]

  (cal-position-1 moon-sample1 365)
  ;; => #object[Vector3 [-0.8715511830075957 -0.4891863172862186 0.8012841458421649]]

  (cal-position moon-sample1 300)
  ;; => #object[Vector3 [0.12328064730424265 0.16499015609975917 -1.2643342637097288]]

  (cal-position-without-axis-precession moon-sample1 365)
  ;; => #object[Vector3 [-0.9172098158619132 -0.4051743820844884 0.7971956308140475]]

  (cal-position-with-axis-precession moon-sample1 365)
  ;; => #object[Vector3 [-0.9024143502132218 -0.4397705673035338 0.7957456808948292]]


  (let [q1 (q/from-unit-vectors (v3/from-seq [0 1 0]) lunar-axis)]
    (v3/apply-quaternion (v3/from-seq [0 0 -1.281]) q1))
;; => #object[Vector3 [-0.016974988456277856 0.09411960646215528 -1.2774248899431686]]
  )