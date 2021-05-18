(ns shu.goog.test-vec3
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [shu.goog.vec3 :as gv3]))


(def zero (vector 0 0 0))
(def v1 (gv3/vector3 1.1 2.2 3.3))
(def v2 (gv3/vector3 1 2 3))

(gv3/add v1 v2)
  ;; => #object[Object (2.1, 4.2, 6.3)]

(def v3 v1)

(gv3/clone' v1)
  ;; => #object[Object (1.1, 2.2, 3.3)]

(gv3/equals (gv3/vector3 1 2 3) (gv3/vector3 1.0 2.0 3.0))
(gv3/equals (gv3/vector3 1 2 3) (gv3/vector3 (+ 1.0 1e-16) 2.0 3.0))

(gv3/invert v1)
  ;; => #object[Object (-1.1, -2.2, -3.3)]

(gv3/magnitude v1)
  ;; => 4.115823125451335

(gv3/normalize v1)
  ;; => #object[Object (0.26726124191242445, 0.5345224838248489, 0.8017837257372732)]

(gv3/scale v1 3)
  ;; => #object[Object (3.3000000000000003, 6.6000000000000005, 9.899999999999999)]

(gv3/squared-magnitude v1)
  ;; => 16.939999999999998

(gv3/subtract v1 v2)
  ;; => #object[Object (0.10000000000000009, 0.20000000000000018, 0.2999999999999998)]

(gv3/to-string v1)
  ;; => "(1.1, 2.2, 3.3)"

(gv3/cross v1 v2)
  ;; => #object[Object (8.881784197001252e-16, -4.440892098500626e-16, 0)]

(gv3/difference v1 v2)
  ;; => #object[Object (0.10000000000000009, 0.20000000000000018, 0.2999999999999998)]

(gv3/difference v1 v2)
  ;; => #object[Object (0.10000000000000009, 0.20000000000000018, 0.2999999999999998)]

(gv3/dot v1 v2)
  ;; => 15.399999999999999

(gv3/lerp v1 v2 0.5)
  ;; => #object[Object (1.05, 2.1, 3.15)]

(gv3/random)
  ;; => #object[Object (-0.7284797344594393, 0.4157338998555294, 0.26871710167444607)]

(gv3/random-unit)
  ;; => #object[Object (-0.4071168661218624, -0.39478615444441145, -0.8236502592594183)]

(gv3/rescaled v1 3)
  ;; => #object[Object (3.3000000000000003, 6.6000000000000005, 9.899999999999999)]

(gv3/squared-distance v1 v2)
  ;; => 0.13999999999999999

(gv3/distance v1 v2)
  ;; => 0.3741657386773941

(gv3/equals (gv3/sum v1 v2) (gv3/add v1 v2))

(gv3/vec3 [1 2 3])

