(ns astronomy.model.spin
  (:require
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [shu.three.matrix4 :as m4]))



;; data

(def sample1
  #:spin {:axis [0 1 0]
          :angular-velocity (* 2 Math/PI)})


(def schema {})

;; model

(defn period-to-angular-velocity [period]
  (/ (* 2 Math/PI) period))


(defn cal-quaternion [spin days]
  (let [tilt-matrix (m4/make-rotation-from-quaternion
                     (q/from-unit-vectors
                      (v3/vector3 0 1 0)
                      (v3/from-seq (:spin/axis spin))))
        rotation-angle (* (:spin/angular-velocity spin) days)
        spin-matrix (m4/make-rotation-axis (v3/vector3 0 1 0) rotation-angle)
        rotation-matrix (m4/multiply tilt-matrix spin-matrix)]
    (q/from-rotation-matrix rotation-matrix)))

(defn cal-tilt-quaternion [spin]
  (q/from-unit-vectors
   (v3/vector3 0 1 0)
   (v3/normalize (v3/from-seq (:spin/axis spin)))))

(defn cal-tilt-matrix [spin days]
  (m4/make-rotation-from-quaternion (q/from-unit-vectors
                                     (v3/vector3 0 1 0)
                                     (v3/normalize (v3/from-seq (:spin/axis spin))))))

(defn cal-self-spin-matrix [spin days]
  (let [rotation-angle (* (:spin/angular-velocity spin) days)
        self-spin-matrix (if spin
                           (m4/make-rotation-axis (v3/vector3 0 1 0) rotation-angle)
                           (m4/identity-matrix4))]
    self-spin-matrix))



(comment
  (cal-quaternion sample1 2)
  )