(ns shu.goog.test-matrix
  (:require
   [shu.goog.vec2 :as vec2]
   [shu.goog.vec3 :as vec3]
   [shu.goog.matrix :as m]
   [cljs.test :refer-macros [deftest is run-tests]]))

;;   (add [this] [this m])
;;   (append-columns [this m])
;;   (append-rows [this m])
;;   (equals [this m] [this m opt_tolerance])
;;   (determinant [this])
;;   (inverse [this])
;;   (reduced-row-echelon-form [this])
;;   (size [this])
;;   (transpose [this])
;;   (get-value-at [this i j])
;;   (square? [this])
;;   (multiply [this m])
;;   (multiply-vec [this v])
;;   (subtract [this m])
;;   (to-js-array [this])
;;   (to-string [this]))

(def m1 (m/matrix [[1 0]
                   [3 1]]))

(def m2 (m/matrix [[5 6]
                   [7 8]]))

(def m3 (m/matrix [[1 2 3]
                   [1 2 3]
                   [4 6 7]]))

(def v1 (vec2/vector2 1 2))
(def v3 (vec3/vector3 1 2 3))


(deftest test-matrix
  (let [om1 m1
        om2 m2
        om3 m3
        ov1 v1
        ov3 v3]
    (is (m/equals (m/add m1 m2) (m/matrix [[6 6] [10 9]])))

    (is (m/equals (m/append-rows m1 m2)
                  (m/matrix [[1 0]
                             [3 1]
                             [5 6]
                             [7 8]])))

    (is (m/equals (m/append-columns m1 m2)
                  (m/matrix [[1 0 5 6]
                             [3 1 7 8]])))

    (is (= (m/determinant m1) 1))

    (is (m/equals (m/multiply (m/inverse m1) m1) (m/create-identity 2)))
    (is (m/equals (m/multiply (m/inverse m2) m2) (m/create-identity 2)))

    (is (= (m/size m1) [2 2]))

    (is (m/equals (m/transpose m1) (m/matrix [[1 3] [0 1]])))
    (is (m/equals (-> m1 m/transpose m/transpose) m1))

    ;; [[1 0]
    ;;  [3 1]]
    (is (= (m/get-value-at m1 0 0) 1))
    (is (= (m/get-value-at m1 0 1) 0))

    (is (m/square? m1))
    (is (m/square? m3))

    (is (m/equals (m/multiply m1 m1)
                  (m/matrix [[1 0]
                             [6 1]])))


    (is (= (seq m1) [[1 0] [3 1]]))

    (is (vec2/equals (m/multiply-vec m1 v1) (vec2/vector2 1 5)))
    (is (vec3/equals (m/multiply-vec m3 v3) (vec3/vector3 14 14 37)))

    (is (m/equals (m/subtract m1 m2)
                  (m/matrix [[-4 -6]
                             [-4 -7]])))
    
    (= (js->clj (m/to-js-array m1)) [[1 0] [3 1]])

    (is (m/equals om1 m1))
    (is (m/equals om2 m2))
    (is (m/equals om3 m3))
    (is (vec2/equals ov1 v1))
    (is (vec3/equals ov3 v3))

    ;; 
    ))


(run-tests)