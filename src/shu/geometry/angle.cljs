(ns shu.geometry.angle
  (:require
   [goog.math :as gmath]))

;; 角度有三种表示， 度数、弧度、小时，其中度数和小时还可以parse到minutes和seconds
;; #:angle{:degree 300}


(defn standard-angle-in-degrees
  ";; standardAngle (angle) → number
   Normalizes an angle to be in range [0-360). Angles outside this range will be normalized to be the equivalent angle with that range."
  [angle]
  (gmath/standardAngle angle))

(defn to-degrees
  ";; toDegrees (angleRadians) → number"
  [angle-in-radians]
  (gmath/toDegrees angle-in-radians))

(def convert-radians-to-degrees to-degrees)

(defn gen-degrees [parsed-degrees]
  (let [{:keys [degree minute second]} parsed-degrees]
    (+ degree
       (/ minute 60)
       (/ second 3600))))

(defn parse-degrees [angle-in-degrees]
  (let [degree (int angle-in-degrees)
        rem-degree (rem angle-in-degrees 1)
        minute (int (* 60 rem-degree))
        rem-minute (rem (* 60 rem-degree) 1)
        second (* rem-minute 60)]
    {:degree degree
     :minute minute
     :second second}))

;; 2 弧度制

(defn standard-angle-in-radians
  ";; standardAngleInRadians (angle) → number
   Normalizes an angle to be in range [0-2*PI). Angles outside this range will be normalized to be the equivalent angle with that range."
  [angle-in-radians]
  (gmath/standardAngleInRadians angle-in-radians))

(defn to-radians
  ";; toRadians (angleDegrees) → number"
  [angle-in-degree]
  (gmath/toRadians angle-in-degree))

(def convert-degrees-to-radians to-radians)

;; 3. 天文学的经度使用的小时制，一个圆周是24小时

(defn convert-degrees-to-hours [angle-in-degrees]
  (* 24 (/ angle-in-degrees 360)))

(defn convert-hours-to-degrees [angle-in-hours]
  (* 360 (/ angle-in-hours 24)))

(defn parse-hours [angle-in-degrees]
  (let [hour (int angle-in-degrees)
        rem-hour (rem angle-in-degrees 1)
        minute (int (* 60 rem-hour))
        rem-minute (rem (* 60 rem-hour) 1)
        second (* rem-minute 60)]
    {:hour hour
     :minute minute
     :second second}))

(defn gen-hours [parsed-degrees]
  (let [{:keys [hour minute second]} parsed-degrees]
    (+ hour
       (/ minute 60)
       (/ second 3600))))



(comment 
  
  (gen-degrees (parse-degrees 34553.3466793))

  (convert-hours-to-degrees (convert-degrees-to-hours 34.2394623))

  (int -5.2)
  (rem -5.4 1)


  ;; 
  )