(ns shu.general.core)


(defn scale-pi [n]
  (* n Math/PI))

(defn almost-zero? [n]
  (< (Math/abs n) 1e-10))

(defn almost-equal? [a b]
  (cond
   (and (number? a) (number? b) (< (Math/abs (- a b)) 1e-10)) true
   (and (seq a) (seq b)
        (every? almost-zero? (map - (seq a) (seq b)))) true
   :else false))

(defn degree-to-radian [degree]
  (* degree (/ Math/PI 180)))

(def ->radian degree-to-radian)

(defn radian-to-degree [radian]
  (/ radian (/ Math/PI 180)))

(def ->degree radian-to-degree)

(defn rand-sign []
  (if (< (rand) 0.5) -1 1))