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

;;   "有问题，需要调试"
(defn cal-position-quaternion [ellipse-orbit]
  (let [{:ellipse-orbit/keys [inclination-in-degree
                              longitude-of-the-ascending-node-in-degree
                              argument-of-periapsis-in-degree]} ellipse-orbit
        q1 (q/from-axis-angle (v3/vector3 0 1 0) (gmath/to-radians argument-of-periapsis-in-degree))
        q2 (q/from-axis-angle (v3/vector3 0 0 -1) (gmath/to-radians inclination-in-degree))
        q3 (q/from-axis-angle (v3/vector3 0 1 0) (gmath/to-radians longitude-of-the-ascending-node-in-degree))
        q4 (q/from-axis-angle (v3/vector3 0 0 1) (gmath/to-radians 23.4))]
    (-> (q/quaternion)
        (q/multiply q1)
        (q/multiply q2)
        (q/multiply q3)
        (q/multiply q4))))

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

#_(defn cal-position-vector [ellipse-orbit days]
  (let [p (cal-position-on-plane ellipse-orbit days)
        q (cal-position-quaternion ellipse-orbit)]
    (v3/apply-quaternion (v3/from-seq p) q)))

(defn cal-position [ellipse-orbit days]
  (seq (cal-position-vector ellipse-orbit days)))

(defn cal-orbit-points-vectors [ellipse-orbit points-number]
  (for [i (range (inc points-number))]
    (let [days (* (:orbit/period ellipse-orbit) i (/ 1 points-number))]
      (cal-position-vector ellipse-orbit days))))


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
  
  )