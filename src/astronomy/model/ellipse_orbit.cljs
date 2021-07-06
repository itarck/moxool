(ns astronomy.model.ellipse-orbit
  (:require
   [shu.goog.math :as gmath]
   [shu.three.euler :as e]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]))


;; data

(def schema {})


(defn period-to-angular-velocity [period]
  (/ (* 2 Math/PI) period))

(defn period-to-angular-velocity-in-degree [period]
  (/ 360 period))

(comment 
  
  (def sample1
    #:ellipse-orbit {:semi-major-axis 193.1642156
                     :eccentricity 0.205630
                     :inclination-in-degree 7.005
                     :longitude-of-the-ascending-node-in-degree 48.331
                     :argument-of-periapsis-in-degree 29.124
                     :start-position-angle-in-degree 0
                     :angular-velocity-in-degree (period-to-angular-velocity-in-degree 87.97)
                     :orbit/type :ellipse-orbit
                     :orbit/period 87.97
                     :circle-orbit/color "white"
                     :circle-orbit/show? true
                     :circle-orbit/radius 193.1642156
                     :circle-orbit/period 87.97})

  (def earth-sample 
    #:ellipse-orbit{:semi-major-axis 499.0052919
                    :eccentricity 0.0167086
                    :inclination-in-degree 0.00005
                    :longitude-of-the-ascending-node-in-degree -11.26064
                    :argument-of-periapsis-in-degree 114.20783
                    :start-position-angle-in-degree 358.617
                    :angular-velocity-in-degree (period-to-angular-velocity-in-degree 365.2564)

                    :orbit/type :ellipse-orbit
                    :orbit/period 365.2564
                    :orbit/color "green"
                    :orbit/show? true})
  
  
  )

(defn current-angular-in-degree [ellipse-orbit days]
  (let [{:ellipse-orbit/keys [start-position-angle-in-degree angular-velocity-in-degree]} ellipse-orbit]
    (+ (* angular-velocity-in-degree days) start-position-angle-in-degree)))

(defn cal-semi-focal-length [ellipse-orbit]
  (let [{:ellipse-orbit/keys [semi-major-axis eccentricity]} ellipse-orbit]
    (* semi-major-axis eccentricity)))

(defn cal-semi-minor-axis [ellipse-orbit]
  (let [a (:ellipse-orbit/semi-major-axis ellipse-orbit)
        c (cal-semi-focal-length ellipse-orbit)]
    (Math/sqrt (- (* a a) (* c c)))))

(defn cal-position-on-plane [ellipse-orbit days]
  (let [a (:ellipse-orbit/semi-major-axis ellipse-orbit)
        c (cal-semi-focal-length ellipse-orbit)
        b (Math/sqrt (- (* a a) (* c c)))
        theta (gmath/to-radians (current-angular-in-degree ellipse-orbit days))]
    [(* -1 b (Math/sin theta)) 0 (+ (* -1 a (Math/cos theta)) c)]))


(defn cal-position-vector [ellipse-orbit days]
  (let [p (cal-position-on-plane ellipse-orbit days)
        {:ellipse-orbit/keys [inclination-in-degree
                              longitude-of-the-ascending-node-in-degree
                              argument-of-periapsis-in-degree]} ellipse-orbit
        q1 (q/from-axis-angle (v3/vector3 0 1 0) (gmath/to-radians argument-of-periapsis-in-degree))
        q2 (q/from-axis-angle (v3/vector3 0 0 -1) (gmath/to-radians inclination-in-degree))
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


(defn cal-true-anomaly [eccentricity mean-anomaly]
  ;; https://en.wikipedia.org/wiki/Mean_anomaly
  (let [m mean-anomaly
        e eccentricity]
    (+ m
       (* (- (* 2 e) (* 0.25 (Math/pow e 3))) (Math/sin m))
       (* 1.25 (Math/pow e 2) (Math/sin (* 2 m)))
       (* (/ 13 12) (Math/pow e 3) (Math/sin (* 3 m))))))


(comment

  (current-angular-in-degree sample1 180)

  (cal-position-on-plane sample1 0)


  (cal-position-on-plane sample1 0)
  ;; => [1.21695 0 0]

  (cal-position-on-plane sample1 180)
  ;; => [-1.3450499999999999 0 -1.566810356882668E-16]

  (cal-position-on-plane sample1 270)

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


  )