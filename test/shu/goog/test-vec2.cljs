(ns shu.goog.test-vec2
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [shu.goog.vec2 :as gv2]
   [shu.general.core :as g]))


;; (add [this] [this b])
;; (clone' [this])
;; (equals [this other])
;; (invert [this])
;; (magnitude [this])
;; (normalize [this])
;; (rotate-degrees [this degrees] [this degrees opt_center])
;; (rotate-radians [this radians] [this radians opt_center])
;; (scale [this sx] [this sx opt_sy])
;; (squared-magnitude [this])
;; (subtract [this b])
;; (to-string [this])
;; (translate [this tx] [this tx opt_ty])


(def zero (gv2/vector2 0 0))
(def v1 (gv2/vector2 1.1 0))
(def v2 (gv2/vector2 3.1 5.1))
(def v3 (gv2/vector2 4.2 5.1))
(def v4 (gv2/vector2 3 0))

(deftest test-vector2
  (let [ov1 (gv2/clone' v1)
        ov2 (gv2/clone' v2)
        ov4 (gv2/clone' v4)]
    (is (gv2/equals (gv2/add v1 v2) v3))
    (is (gv2/equals (gv2/clone' v1) v1))
    (is (gv2/equals (gv2/add (gv2/invert v1) v1) zero))
    (is (= (gv2/magnitude (gv2/vector2 3 0)) 3))
    (is (gv2/equals (gv2/normalize v4) (gv2/vector2 1 0)))
    (is (gv2/equals (gv2/rotate-degrees (gv2/vector2 1 0) 90) (gv2/vector2 0 1)))
    (is (gv2/equals (gv2/rotate-radians (gv2/vector2 1 0) (/ Math/PI 2)) (gv2/vector2 0 1)))
    (is (gv2/equals (gv2/scale v1 3) (gv2/vector2 3.3 0)))
    (is (gv2/equals (gv2/scale v2 3 2) (gv2/vector2 9.3 10.2)))
    (is (= (gv2/squared-magnitude v4) 9))
    (is (gv2/equals (gv2/subtract v1 v2) (gv2/vector2 -2 -5.1)))
    (is (= (gv2/to-string v1) (str v1)))

    (is (gv2/equals (gv2/translate v1 v2) (gv2/add v1 v2)))

    (is (gv2/equals v1 ov1))
    (is (gv2/equals v2 ov2))
    (is (gv2/equals v4 ov4))))


;; (defn determinant [a b] (Vec2.determinant a b))
;; (defn difference [a b] (Vec2.difference a b))
;; (defn distance [a b] (Vec2.distance a b))
;; (defn dot [a b] (Vec2.dot a b))
;; (defn from-coordinate [a] (Vec2.fromCoordinate a))
;; (defn lerp [a b x] (Vec2.lerp a b x))
;; (defn random [] (Vec2.random))
;; (defn random-unit [] (Vec2.randomUnit))

(deftest test-vector2-functions
  (let [ov1 (gv2/clone' v1)
        ov2 (gv2/clone' v2)
        ov4 (gv2/clone' v4)]

    (is (= (Math/abs (gv2/determinant (gv2/vector2 0 1) (gv2/vector2 1 0))) 1))
    (is (= (gv2/distance v1 zero) (gv2/magnitude v1)))
    (is (< (Math/abs (gv2/dot (gv2/rotate-degrees v1 90) v1)) 1e-10))
    (is (gv2/equals (gv2/lerp v1 zero 0.5)  (gv2/scale v1 0.5)))
    (is (gv2/equals (gv2/lerp zero v1 0.3)  (gv2/scale v1 0.3)))
    (is (= (count (gv2/random)) 2))
    (is (g/almost-equal? (gv2/magnitude (gv2/random-unit)) 1))

    (is (gv2/equals v1 ov1))
    (is (gv2/equals v2 ov2))
    (is (gv2/equals v4 ov4))
    ;; 
    ))


(run-tests)

