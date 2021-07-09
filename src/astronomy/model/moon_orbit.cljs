(ns astronomy.model.moon-orbit
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

ecliptic-axis
;; => #object[Vector3 [-0.3977771559301344 0.9174820620699532 0]]

(defn period-to-angular-velocity [period]
  (/ (* 2 Math/PI) period))

(defn period-to-angular-velocity-in-degree [period]
  (/ 360 period))


(def lunar-axis-j2000
  (v3/apply-axis-angle
   (from-ecliptic-to-equatorial (cal-vector 192 84.85))
   ecliptic-axis
   (* (dt/to-epoch-days (t/date-time 2010 7 2))
      (period-to-angular-velocity 6798))))


(def moon-sample1
  #:moon-orbit {:axis (seq lunar-axis-j2000)
                :axis-precession-center (seq ecliptic-axis)
                :axis-precession-velocity (period-to-angular-velocity-in-degree -6798)

                :epoch-days-j20110615 4183.343103981481

                :semi-major-axis 1.352270908
                :eccentricity 0.0549
                :inclination 5.145

                :longitude-of-the-ascending-node-j20110615 -96.47355839952931
                :argument-of-periapsis-j20110615 282.31
                :mean-anomaly-j20110615 73.9

                :angular-velocity (period-to-angular-velocity-in-degree 27.321661)
                :anomaly-angular-velocity (period-to-angular-velocity-in-degree 27.554549886)
                :perigee-angular-velocity-eme2000 (period-to-angular-velocity-in-degree 3233)
                :perigee-angular-velocity-emo2000 (period-to-angular-velocity-in-degree 2191)
                :nodical-angular-velocity (period-to-angular-velocity-in-degree 27.21222082)
                :anomaly-month 27.554549886
                :nodical-month 27.21222082

                :orbit/type :moon-orbit
                :orbit/color "white"
                :orbit/show? false
                :orbit/period 27.321661})


(defn cal-semi-focal-length [moon-orbit]
  (let [{:moon-orbit/keys [semi-major-axis eccentricity]} moon-orbit]
    (* semi-major-axis eccentricity)))

(defn cal-semi-minor-axis [moon-orbit]
  (let [a (:moon-orbit/semi-major-axis moon-orbit)
        c (cal-semi-focal-length moon-orbit)]
    (Math/sqrt (- (* a a) (* c c)))))

(defn cal-current-longitude-of-the-ascending-node [moon-orbit epoch-days]
  (let [{:moon-orbit/keys [epoch-days-j20110615 longitude-of-the-ascending-node-j20110615 axis-precession-velocity]} moon-orbit]
    (+ longitude-of-the-ascending-node-j20110615
       (* axis-precession-velocity (- epoch-days epoch-days-j20110615)))))

(defn cal-current-longitude-of-periapsis-eme [moon-orbit epoch-days]
  (let [{:moon-orbit/keys [argument-of-periapsis-j2000 perigee-angular-velocity-eme2000]} moon-orbit]
    (+ argument-of-periapsis-j2000
       (* perigee-angular-velocity-eme2000 epoch-days))))

(defn cal-current-argument-of-periapsis-emo [moon-orbit epoch-days]
  (let [{:moon-orbit/keys [perigee-angular-velocity-emo2000 argument-of-periapsis-j20110615 epoch-days-j20110615]} moon-orbit]
    (+ argument-of-periapsis-j20110615
       (* perigee-angular-velocity-emo2000 (- epoch-days epoch-days-j20110615)))))

(defn cal-true-anomaly [eccentricity mean-anomaly]
  ;; https://en.wikipedia.org/wiki/Mean_anomaly
  (let [m (gmath/to-radians mean-anomaly)
        e eccentricity
        f (+ m
             (* (- (* 2 e) (* 0.25 (Math/pow e 3))) (Math/sin m))
             (* 1.25 (Math/pow e 2) (Math/sin (* 2 m)))
             (* (/ 13 12) (Math/pow e 3) (Math/sin (* 3 m))))]
    (gmath/to-degree f)))

(defn cal-mean-anomaly [eccentricity true-anomaly]
  ;; https://en.wikipedia.org/wiki/Mean_anomaly
  (let [f (gmath/to-radians true-anomaly)
        e eccentricity
        m (+ f
             (* -2 e (Math/sin f))
             (* (+ (* 0.75 (Math/pow e 2)) (* 0.125 (Math/pow e 4)))
                (Math/sin (* 2 f)))
             (* (/ 1 -3) (Math/pow e 3) (Math/sin (* 3 f)))
             (* (/ 5 32) (Math/pow e 4) (Math/sin (* 4 f))))]
    (gmath/to-degree m)))

(defn cal-radius [semi-major-axis eccentricity true-anomaly]
  ;; https://en.wikipedia.org/wiki/True_anomaly
  (let [a semi-major-axis
        e eccentricity
        f true-anomaly]
    (* a (/ (- 1 (Math/pow e 2))
            (+ 1 (* e (Math/cos f)))))))

(defn cal-current-mean-anomaly [moon-orbit epoch-days]
  (let [{:moon-orbit/keys [anomaly-month anomaly-angular-velocity mean-anomaly-j20110615 epoch-days-j20110615]} moon-orbit
        rem-epoch-days (rem (- epoch-days epoch-days-j20110615) anomaly-month)
        mean-anomaly (+ mean-anomaly-j20110615 (* anomaly-angular-velocity rem-epoch-days))]
    mean-anomaly))

(defn cal-position-to-perigee [moon-orbit epoch-days]
  (let [{:moon-orbit/keys [semi-major-axis eccentricity]} moon-orbit
        current-mean-anomaly (cal-current-mean-anomaly moon-orbit epoch-days)
        current-true-anomaly (cal-true-anomaly eccentricity current-mean-anomaly)
        radius (cal-radius semi-major-axis eccentricity (gmath/to-radians current-true-anomaly))
        position (v3/from-spherical-coords radius (/ Math/PI 2) (gmath/to-radians current-true-anomaly))]
    position))

(defn cal-position-vector [moon-orbit epoch-days]
  (let [p (cal-position-to-perigee moon-orbit epoch-days)
        {:moon-orbit/keys [inclination]} moon-orbit
        current-longitude-of-the-ascending-node (cal-current-longitude-of-the-ascending-node moon-orbit epoch-days)
        current-argument-of-periapsis (cal-current-argument-of-periapsis-emo moon-orbit epoch-days)
        q1 (q/from-axis-angle (v3/vector3 0 1 0) (gmath/to-radians current-argument-of-periapsis))
        q2 (q/from-axis-angle (v3/vector3 0 0 1) (gmath/to-radians inclination))
        q3 (q/from-axis-angle (v3/vector3 0 1 0) (gmath/to-radians current-longitude-of-the-ascending-node))
        q4 (q/from-axis-angle (v3/vector3 0 0 1) (gmath/to-radians ecliptic-angle))]
    (-> p
        (v3/apply-quaternion q1)
        (v3/apply-quaternion q2)
        (v3/apply-quaternion q3)
        (v3/apply-quaternion q4))))


