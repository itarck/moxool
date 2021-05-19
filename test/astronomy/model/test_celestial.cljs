(ns astronomy.model.test-celestial
  (:require
   [shu.three.matrix4 :as m4]
   [astronomy.test-conn :refer [create-poshed-conn! create-test-conn!]]
   [astronomy.model.celestial :as m.celestial]))


(def celestial-1
  #:celestial
   {:orbit #:circle-orbit {:start-position [0 0 100]
                           :axis [-1 1 0]
                           :period 360}
    :spin #:spin {:axis [1 1 1]
                  :period 30}})

(def clock-1
  #:clock {:name "default"
           :time-in-days 1})


(m.celestial/cal-position celestial-1 90)


(m.celestial/cal-matrix celestial-1 1)
  ;; => #object[Matrix4 
  ;;           1, 0, -2.4492935982947064e-16, -8.606678077917342
  ;;           0, 1, 0, 0
  ;;           2.4492935982947064e-16, 0, 1, -499.92591960455815
  ;;           0, 0, 0, 1]

(m.celestial/cal-matrix celestial-1 2)
  ;; => #object[Matrix4 
  ;;           1, 0, -4.898587196589413e-16, -17.21080581137287
  ;;           0, 1, 0, 0
  ;;           4.898587196589413e-16, 0, 1, -499.70370036985247
  ;;           0, 0, 0, 1]

(m.celestial/cal-position-matrix celestial-1 1)
  ;; => #object[Matrix4 
  ;;           1, 0, 0, -8.606678077917342
  ;;           0, 1, 0, 0
  ;;           0, 0, 1, -499.92591960455815
  ;;           0, 0, 0, 1]


(m.celestial/cal-spin-matrix celestial-1 1)
  ;; => #object[Matrix4 
  ;;           1, 0, -2.4492935982947064e-16, 0
  ;;           0, 1, 0, 0
  ;;           2.4492935982947064e-16, 0, 1, 0
  ;;           0, 0, 0, 1]

(m.celestial/update-position-tx celestial-1 clock-1)


(m.celestial/update-quaternion-tx celestial-1 clock-1)
  ;; => [[:db/add nil :celestial/quaternion [0 0.10452846326765346 0 0.9945218953682733]]]

(m.celestial/cal-position celestial-1 90)
  ;; => #object[Vector3 [-168.76144979705663 337.52289959411326 -411.9759762692706]]

(m.celestial/cal-quaternion celestial-1 30)
  ;; => #object[Quaternion [0 -1.2246467991473532e-16 0 1]]


(def test-conn (create-poshed-conn!))


(m.celestial/find-celestials-by-clock test-conn {:db/id [:clock/name "default"]})
;; => [{:object/scene #:db{:id 1}, :celestial/clock #:db{:id 2}, :planet/chinese-name "地球", :entity/type :planet, :planet/name "earth", :celestial/gltf #:db{:id 5}, :planet/star #:db{:id 3}, :object/position [100 0 0], :planet/radius 2, :db/id 4, :celestial/spin {:db/id 6, :spin/axis [0 1 0], :spin/period 1}, :planet/color "blue", :celestial/orbit {:db/id 7, :circle-orbit/axis [-1 2 0], :circle-orbit/period 365, :circle-orbit/star [:star/name "sun"], :circle-orbit/start-position [100 0 0]}} {:object/scene #:db{:id 1}, :celestial/clock #:db{:id 2}, :satellite/planet #:db{:id 4}, :satellite/radius 1, :satellite/chinese-name "月球", :entity/type :satellite, :celestial/gltf #:db{:id 9}, :object/position [0 0 30], :satellite/name "moon", :db/id 8, :celestial/spin {:db/id 10, :spin/axis [0 1 0], :spin/period 1}, :satellite/color "gray", :celestial/orbit {:db/id 11, :circle-orbit/axis [1 2 0], :circle-orbit/period 30, :circle-orbit/start-position [0 0 30]}}]

(m.celestial/update-position-and-quaternion-tx celestial-1 clock-1)
;; => [[:db/add nil :celestial/position [-8.606678077917342 17.213356155834685 -499.7777588136744]] [:db/add nil :celestial/quaternion [0 0.10452846326765346 0 0.9945218953682733]]]


(m.celestial/cal-position celestial-1 180)
;; => #object[Vector3 [4.302223300453059 0 -99.907411510223]]


(def mt1 (m.celestial/cal-position-matrix celestial-1 45.2))
;; => #object[Matrix4 
;;           1, 0, 0, 62.78115789747651
;;           0, 1, 0, -31.390578948738256
;;           0, 0, 1, 71.22610312459298
;;           0, 0, 0, 1]


(def mt2 (m.celestial/cal-tilt-matrix celestial-1 45.2))
;; => #object[Matrix4 
;;           0.6666666666666665, 0.6666666666666669, -0.3333333333333334, 0
;;           -0.6666666666666669, 0.33333333333333315, -0.6666666666666669, 0
;;           -0.3333333333333334, 0.6666666666666669, 0.6666666666666665, 0
;;           0, 0, 0, 1]

(def mt3 (m.celestial/cal-self-spin-matrix celestial-1 45.2))
;; => #object[Matrix4 
;;           -0.9991228300988583, 0, -0.041875653729200206, 0
;;           0, 1, 0, 0
;;           0.041875653729200206, 0, -0.9991228300988583, 0
;;           0, 0, 0, 1]

(def mt0 (m.celestial/cal-matrix celestial-1 45.2))
;; => #object[Matrix4 
;;           -0.6800404379756391, 0.6666666666666669, 0.30512384088015293, 62.78115789747651
;;           0.638164784246439, 0.3333333333333328, 0.6939989892187058, -31.390578948738256
;;           0.3609580458524198, 0.6666666666666669, -0.6521233354895055, 71.22610312459298
;;           0, 0, 0, 1]


(m4/multiply (m4/multiply mt1 mt2) mt3)
;; => #object[Matrix4 
;;           -0.6800404379756388, 0.6666666666666669, 0.30512384088015276, 62.78115789747651
;;           0.638164784246439, 0.33333333333333315, 0.6939989892187058, -31.390578948738256
;;           0.36095804585241964, 0.6666666666666669, -0.6521233354895053, 71.22610312459298
;;           0, 0, 0, 1]


(comment

  (def conn (create-test-conn!))

  (def db @conn)

  (def celes (m.celestial/find-all-by-clock db [:clock/name "default"]))

  (m.celestial/update-position-tx (first celes))

  )