(ns astronomy.model.ellipse-orbit
  (:require
   [shu.goog.math :as gmath]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]))


;; data

(def schema {})


(defn period-to-angular-velocity [period]
  (/ (* 2 Math/PI) period))

(defn period-to-angular-velocity-in-degree [period]
  (/ 360 period))

  
(def sample1
  #:ellipse-orbit {:semi-major-axis 193.1642156
                   :eccentricity 0.205630
                   :inclination-in-degree 7.005
                   :longitude-of-the-ascending-node-in-degree 48.331
                   :argument-of-periapsis-in-degree 29.124
                   :mean-anomaly-in-degree 0
                   :angular-velocity-in-degree (period-to-angular-velocity-in-degree 87.97)
                   :orbit/type :ellipse-orbit
                   :orbit/period 87.97
                   :circle-orbit/color "white"
                   :circle-orbit/show? true
                   :circle-orbit/radius 193.1642156
                   :circle-orbit/period 87.97})

(def earth-sample
  #:ellipse-orbit{:semi-major-axis 499.0052919
                  :eccentricity 0.01671022
                  :inclination-in-degree 0.00005
                  :longitude-of-the-ascending-node-in-degree -11.26064
                  :argument-of-periapsis-in-degree 114.20783
                  :mean-anomaly-in-degree 357.51716
                  :angular-velocity-in-degree (period-to-angular-velocity-in-degree 365.256363004)

                  :orbit/type :ellipse-orbit
                  :orbit/period 365.256363004
                  :orbit/color "green"
                  :orbit/show? true})


(defn cal-semi-focal-length [ellipse-orbit]
  (let [{:ellipse-orbit/keys [semi-major-axis eccentricity]} ellipse-orbit]
    (* semi-major-axis eccentricity)))

(defn cal-semi-minor-axis [ellipse-orbit]
  (let [a (:ellipse-orbit/semi-major-axis ellipse-orbit)
        c (cal-semi-focal-length ellipse-orbit)]
    (Math/sqrt (- (* a a) (* c c)))))

(defn cal-true-anomaly [eccentricity mean-anomaly]
  ;; https://en.wikipedia.org/wiki/Mean_anomaly
  (let [m mean-anomaly
        e eccentricity]
    (+ m
       (* (- (* 2 e) (* 0.25 (Math/pow e 3))) (Math/sin m))
       (* 1.25 (Math/pow e 2) (Math/sin (* 2 m)))
       (* (/ 13 12) (Math/pow e 3) (Math/sin (* 3 m))))))

(defn cal-radius [semi-major-axis eccentricity true-anomaly]
  ;; https://en.wikipedia.org/wiki/True_anomaly
  (let [a semi-major-axis
        e eccentricity
        f true-anomaly]
    (* a (/ (- 1 (Math/pow e 2))
            (+ 1 (* e (Math/cos f)))))))

(defn cal-current-mean-anomaly-in-radians [mean-anomaly-in-degree angular-velocity-in-degree epoch-days]
  (gmath/to-radians (+ mean-anomaly-in-degree (* angular-velocity-in-degree epoch-days))))

(defn cal-position-to-vernal-equinox [ellipse-orbit epoch-days]
  (let [{:ellipse-orbit/keys [semi-major-axis mean-anomaly-in-degree eccentricity angular-velocity-in-degree]} ellipse-orbit
        mean-anomaly (cal-current-mean-anomaly-in-radians mean-anomaly-in-degree angular-velocity-in-degree epoch-days)
        true-anomaly (cal-true-anomaly eccentricity mean-anomaly)
        radius (cal-radius semi-major-axis eccentricity true-anomaly)
        position (v3/from-spherical-coords radius (/ Math/PI 2) true-anomaly)]
    (seq position)))

(defn cal-position-vector [ellipse-orbit days]
  (let [p (cal-position-to-vernal-equinox ellipse-orbit days)
        {:ellipse-orbit/keys [inclination-in-degree
                              longitude-of-the-ascending-node-in-degree
                              argument-of-periapsis-in-degree]} ellipse-orbit
        q1 (q/from-axis-angle (v3/vector3 0 1 0) (gmath/to-radians argument-of-periapsis-in-degree))
        q2 (q/from-axis-angle (v3/vector3 0 0 1) (gmath/to-radians inclination-in-degree))
        q3 (q/from-axis-angle (v3/vector3 0 1 0) (gmath/to-radians longitude-of-the-ascending-node-in-degree))
        q4 (q/from-axis-angle (v3/vector3 0 0 1) (gmath/to-radians 23.4))]
    (-> (v3/from-seq p)
        (v3/apply-quaternion q1)
        (v3/apply-quaternion q2)
        (v3/apply-quaternion q3)
        (v3/apply-quaternion q4))))

(defn cal-position [ellipse-orbit days]
  (seq (cal-position-vector ellipse-orbit days)))

(defn cal-orbit-points-vectors [ellipse-orbit points-number]
  (for [i (range (inc points-number))]
    (let [days (* (:orbit/period ellipse-orbit) i (/ 1 points-number))]
      (cal-position-vector ellipse-orbit days))))


(comment

  (cal-position sample1 30)

  (cal-position sample1 87)

  (cal-orbit-points-vectors sample1 4)

  (q/from-axis-angle (v3/vector3 0 0 1) (gmath/to-radians (+ 7 23.4)))

  (cal-position-vector sample1 0)
  (cal-orbit-points-vectors sample1 10)

  (->>
   (range 11)
   (map #(* (/ % 10) Math/PI))
   (map #(cal-true-anomaly 0.1 %)))
  ;; => (0 0.9067567546560483 1.6237295584668296 2.0652642220415114 2.2820600333569727 2.40412966012823 2.543012781392136 2.7274909607929576 2.9142748279362345 3.0526650948450875 3.141592653589793)
  ;; => (0 0.3841091607162705 0.7586471525430559 1.116301915567596 1.4533211488628799 1.7694629934615633 2.0669450482735265 2.3491625640961344 2.619826331789553 2.8826886522807933 3.141592653589793)
  ;; => (0 0.3141592653589793 0.6283185307179586 0.9424777960769379 1.2566370614359172 1.5707963267948966 1.8849555921538759 2.199114857512855 2.5132741228718345 2.827433388230814 3.141592653589793)


  (cal-position-vector earth-sample 0)
  ;; => #object[Vector3 [-441.2104749761639 -190.92839178407485 98.16697513462124]]

  (cal-position-vector earth-sample 77)
  ;; => #object[Vector3 [5.482166245307989 2.3722584895551755 -496.8727082469014]]


  (cal-position-vector earth-sample 78)
  ;; => #object[Vector3 [6.397173283217713 2.7682183280058337 -496.8433765198534]]

  (cal-position-vector earth-sample 79)
  ;; => #object[Vector3 [-1.5111700601941565 -0.6540355033083146 -497.0291786548116]]

  (cal-position-vector earth-sample 80)

  (cal-position-to-vernal-equinox earth-sample 0)
  
  (cal-position earth-sample 0)

  ;; 
  )