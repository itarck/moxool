(ns astronomy.model.spin
  (:require
   [shu.goog.math :as gmath]
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [shu.three.matrix4 :as m4]))


(def schema {})

(defn period-to-angular-velocity [period]
  (/ (* 2 Math/PI) period))

;; data

(def sample1
  #:spin {:axis [0 1 0]
          :angular-velocity (* 2 Math/PI)})

(def sample2
  #:spin {:axis [-0.3907311284892737 0.9205048534524404 -7.177614387474334E-17]
          :angular-velocity (* 2 Math/PI)})


(def sample3
  #:spin {:axis [0 1 0]
          :axis-center [-0.3971478906347807 0.9177546256839811 -7.295488395810108E-17]
          :axis-anglar-velocity (period-to-angular-velocity (* 365.25 -25722))
          :angular-velocity (* 2 Math/PI)})




;; model


(defn cal-quaternion-old [spin days]
  (let [tilt-matrix (m4/make-rotation-from-quaternion
                     (q/from-unit-vectors
                      (v3/vector3 0 1 0)
                      (v3/from-seq (:spin/axis spin))))
        rotation-angle (* (:spin/angular-velocity spin) days)
        spin-matrix (m4/make-rotation-axis (v3/vector3 0 1 0) rotation-angle)
        rotation-matrix (m4/multiply tilt-matrix spin-matrix)]
    (q/from-rotation-matrix rotation-matrix)))

(defn cal-quaternion [spin days]
  (let [{:spin/keys [axis-center axis-anglar-velocity]} spin
        tilt-q (q/from-unit-vectors
                (v3/vector3 0 1 0)
                (v3/from-seq (:spin/axis spin)))
        rotation-angle (* (:spin/angular-velocity spin) days)
        spin-q (q/from-axis-angle (v3/vector3 0 1 0) rotation-angle)
        axial-q (if axis-center
                  (q/from-axis-angle (v3/from-seq axis-center) (* axis-anglar-velocity days))
                  (q/identity-quaternion))
        rotation-q (q/multiply axial-q (q/multiply tilt-q spin-q))]
    rotation-q))

(defn cal-axial-quaternion [spin days]
  (let [{:spin/keys [axis-center axis-anglar-velocity]} spin
        axial-q (if axis-center
                  (q/from-axis-angle (v3/from-seq axis-center) (* axis-anglar-velocity days))
                  (q/identity-quaternion))]
    axial-q))

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

(defn cal-spin-axis [right-ascension declination]
  (seq (v3/from-spherical-coords
        1
        (gmath/to-radians (- 90.0 declination))
        (gmath/to-radians right-ascension))))


(comment
  
  (cal-quaternion-old sample2 0.1)
  ;; => #object[Quaternion [-0.06160807986834395 0.30281338693568693 0.1896101731677802 0.9319637758082436]]

  ;; => #object[Quaternion [-0.06160807986834395 0.30281338693568693 0.1896101731677802 0.9319637758082436]]

  (cal-quaternion sample2 0.1)
  ;; => #object[Quaternion [-0.061608079868343935 0.30281338693568693 0.18961017316778017 0.9319637758082436]]


  (cal-spin-axis 270 67)
  ;; => (-0.3907311284892737 0.9205048534524404 -7.177614387474334E-17)

  (cal-spin-axis 257.311 -15.175)

  (cal-spin-axis 270 66.6)
  ;; => (-0.3971478906347807 0.9177546256839811 -7.295488395810108E-17)





  
  )