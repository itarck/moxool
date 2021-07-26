(ns astronomy.model.test-celestial
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [shu.three.matrix4 :as m4]
   [methodology.model.core :as mtd-model]
   [astronomy.model.core :as ast-model]
   [astronomy.data.basic :as d.basic]
   [astronomy.data.celestial :as d.celestial]
   [astronomy.model.celestial :as m.celestial]))

(def schema (merge ast-model/schema
                   mtd-model/schema))


(def clock-1
  #:clock {:name "default"
           :time-in-days 1})

(def celestial-1
  #:celestial
   {:orbit #:circle-orbit {:start-position [0 0 100]
                           :axis [-1 1 0]
                           :period 360}
    :spin #:spin {:axis [1 1 1]
                  :period 30}
    :clock clock-1
    :db/id 120
    })

(s/check-asserts true)

(m.celestial/cal-position celestial-1 90)

(m.celestial/update-position-tx celestial-1)


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


(def test-conn
  (let [conn (d/create-conn schema)]
    (d/transact! conn d.basic/dataset1)
    (d/transact! conn d.celestial/dataset1)
    (d/transact! conn d.celestial/dataset2)
    (d/transact! conn d.celestial/dataset3)
    conn))


(m.celestial/update-position-and-quaternion-tx celestial-1)
;; => [[:db/add 120 :object/position [0 0 100]] [:db/add 120 :object/quaternion [0.4082482904638631 0 -0.4082482904638631 0.8164965809277261]]]


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

  ;; 
  )