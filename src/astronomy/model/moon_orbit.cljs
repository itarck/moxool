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

(def date-time-base1 (t/date-time 2010 7 2))
(def epoch-days-base1 (dt/to-epoch-days date-time-base1))

epoch-days-base1
;; => 3834.5007428703702


(def longitude-of-axis-base1 192)
(def longitude-of-the-ascending-node 160)

(def lunar-axis-in-date-time-1 (from-ecliptic-to-equatorial (cal-vector 192 84.85)))

(def lunar-axis-j2000
  (v3/apply-axis-angle
   (from-ecliptic-to-equatorial (cal-vector 192 84.85))
   ecliptic-axis
   (* (dt/to-epoch-days (t/date-time 2010 7 2))
      (period-to-angular-velocity 6798))))


(def moon-sample1
  #:moon-orbit {:start-position [-0.016974988456277856 0.09411960646215528 -1.2774248899431686]
                :axis (seq lunar-axis-j2000)
                :axis-precession-center (seq ecliptic-axis)
                :axis-precession-velocity (period-to-angular-velocity-in-degree -6798)
                
                :semi-major-axis 1.352270908
                :eccentricity 0.0549
                :inclination 5.145
                :longitude-of-axis-j2000 35.062704829851896
                :longitude-of-the-ascending-node-j2000 125.062704829851896
                :argument-of-periapsis-j2000 93.02187830704196
                :mean-anomaly 0

                :angular-velocity (period-to-angular-velocity-in-degree 27.321661)
                :anomaly-angular-velocity (period-to-angular-velocity-in-degree 27.554549886)
                :perigee-angular-velocity (period-to-angular-velocity-in-degree 3233)
                :nodical-angular-velocity (period-to-angular-velocity-in-degree 27.21222082)
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
  (let [{:moon-orbit/keys [longitude-of-the-ascending-node-j2000 axis-precession-velocity]} moon-orbit]
    (+ longitude-of-the-ascending-node-j2000
       (* axis-precession-velocity epoch-days))))

(defn cal-current-argument-of-periapsis [moon-orbit epoch-days]
  (let [{:moon-orbit/keys [argument-of-periapsis-j2000 perigee-angular-velocity]} moon-orbit]
    (+ argument-of-periapsis-j2000
       (* perigee-angular-velocity epoch-days))))

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

;; 6605.06324287037

(defn cal-current-mean-anomaly [moon-orbit epoch-days]
  (let [{:moon-orbit/keys [nodical-month nodical-angular-velocity eccentricity]} moon-orbit
        argument-of-periapsis (cal-current-argument-of-periapsis moon-orbit epoch-days)
        mean-anomaly-before-periapsis (cal-mean-anomaly eccentricity (- argument-of-periapsis))
        rem-epoch-days (rem (- epoch-days 6605.26324287037) nodical-month)
        mean-anomaly (+ (* nodical-angular-velocity rem-epoch-days) mean-anomaly-before-periapsis)]
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
        current-argument-of-periapsis (cal-current-argument-of-periapsis moon-orbit epoch-days)
        current-longitude-of-the-ascending-node (cal-current-longitude-of-the-ascending-node moon-orbit epoch-days)
        q1 (q/from-axis-angle (v3/vector3 0 1 0) (gmath/to-radians current-argument-of-periapsis))
        q2 (q/from-axis-angle (v3/vector3 0 0 1) (gmath/to-radians inclination))
        q3 (q/from-axis-angle (v3/vector3 0 1 0) (gmath/to-radians current-longitude-of-the-ascending-node))
        q4 (q/from-axis-angle (v3/vector3 0 0 1) (gmath/to-radians ecliptic-angle))]
    (-> p
        (v3/apply-quaternion q1)
        (v3/apply-quaternion q2)
        (v3/apply-quaternion q3)
        (v3/apply-quaternion q4))))




(comment

  (cal-true-anomaly 0.05 60)
  ;; => 65.11547069811513

  (cal-mean-anomaly 0.05 65.11547069811513)

  (->> 270
       (cal-true-anomaly 0.05)
       (cal-mean-anomaly 0.05))

  (cal-true-anomaly 0.05 -34)

  (cal-current-mean-anomaly moon-sample1 0)
  ;; => -86.72904389606774

  (cal-current-mean-anomaly moon-sample1 10)
  ;; => 42.804762722173905

  (cal-current-mean-anomaly moon-sample1 27.554549886)
  ;; => -85.33771985733777

  (cal-position-vector-in-ecliptic moon-sample1 0)

  (cal-position-to-perigee moon-sample1 0)
  ;; => #object[Vector3 [1.245628563988985 8.104834180301754e-17 0.44763717296738736]]

  (cal-position-vector-in-ecliptic  moon-sample1 0)
  ;; => #object[Vector3 [-1.255666252973624 0.0341980278238082 0.41725569389001477]]

  (cal-position-to-perigee moon-sample1 0)

  (cal-position-vector-in-ecliptic  moon-sample1 27)
  ;; => #object[Vector3 [0.12120023112279271 0.0905804462333292 -1.288895267639845]]

  (cal-current-argument-of-periapsis moon-sample1 0)
  ;; => 93.02187830704196

  (cal-current-mean-anomaly moon-sample1 0)
  ;; => -86.72904389606774

  (cal-position-to-perigee moon-sample1 0)
  ;; => #object[Vector3 [1.245628563988985 8.104834180301754e-17 0.44763717296738736]]

  (cal-position-vector-in-ecliptic moon-sample1 0)
  ;; => #object[Vector3 [-0.0000022921049878784693 -2.0637960188152398e-7 1.3236198691626684]]

  (cal-position-vector-in-ecliptic moon-sample1 27.21)
  ;; => #object[Vector3 [-0.10600635841168168 -0.009544741698236374 1.367484076467795]]

  (cal-position-vector-in-ecliptic moon-sample1 27.211)
  ;; => #object[Vector3 [-0.10577202675146812 -0.009523642632086292 1.3684322925101848]]

  (cal-position-vector-in-ecliptic moon-sample1 27.21222082)
  ;; => #object[Vector3 [0.0000010421547702193205 9.383491942622746e-8 1.3655331103736912]]

  (cal-position-vector-in-ecliptic moon-sample1 27.22)
  ;; => #object[Vector3 [1.1295959283635242 0.00021461843047747124 -0.7539674244664454]]


  (gmath/to-degree
   (v3/angle-to
    (cal-position-vector-in-ecliptic moon-sample1 0)
    (cal-position-vector-in-ecliptic moon-sample1 27.21222082)))
  ;; => 1.626981437296651

  ;; => 1.4409278829363494

  
  (gmath/to-degree
   (v3/angle-to
    (cal-position-vector-in-ecliptic moon-sample1 0)
    (cal-position-vector-in-ecliptic moon-sample1 27.21222)))
  ;; => 1.6269928061828551

  ;; => 1.4409385671118609

  (gmath/to-degree (v3/angle-to
                    (cal-position-vector moon-sample1 0)
                    (cal-position-vector moon-sample1 1)))

  (cal-semi-minor-axis moon-sample1)

  (- 1.352270908 (cal-semi-focal-length moon-sample1))
;;   
  )