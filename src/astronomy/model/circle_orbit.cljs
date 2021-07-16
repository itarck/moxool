(ns astronomy.model.circle-orbit
  (:require
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.goog.math :as gmath]
   [shu.geometry.angle :as shu.angle]
   [astronomy.model.const :refer [ecliptic-angle ecliptic-axis] :as const]))

;; 带轴进动的圆形轨道
;; 按照参考点 2010-7-2，黄经 192度，黄纬 84.85度。轴进动的周期是6798天，顺时针方向
;; 这样就可以计算 J2000 的轴位置

;; data

(def schema {})


(defn from-ecliptic-to-equatorial [vector-in-ecliptic]
  (v3/apply-axis-angle vector-in-ecliptic (v3/vector3 0 0 1) (gmath/to-radians ecliptic-angle)))

(defn from-equatorial-to-ecliptic [vector-in-equatorial]
  (v3/apply-axis-angle vector-in-equatorial (v3/vector3 0 0 1) (gmath/to-radians ecliptic-angle)))


(def sample1
  #:circle-orbit {:start-position [0 0 -1.281]
                  :radius 1.281
                  :axis [0 1 0]
                  :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 27)})

(def moon-sample1
  #:circle-orbit {:start-position [-0.016974988456277856 0.09411960646215528 -1.2774248899431686]
                  :radius 1.281
                  :axis (seq const/lunar-axis-j2000)
                  :axis-precession-center (seq ecliptic-axis)
                  :axis-precession-velocity (shu.angle/period-to-angular-velocity-in-radians -6798)
                  :angular-velocity (shu.angle/period-to-angular-velocity-in-radians 27.321661)
                  :draconitic-angular-velocity (shu.angle/period-to-angular-velocity-in-radians 27.212220815)

                  :orbit/type :circle-orbit
                  :orbit/color "white"
                  :orbit/show? false
                  :orbit/period 27.321661})



(defn cal-current-axis [orbit epoch-days]
  (let [{:circle-orbit/keys [axis axis-precession-center axis-precession-velocity]} orbit]
    (if axis-precession-center
      (v3/apply-axis-angle
       (v3/from-seq axis)
       (v3/from-seq axis-precession-center)
       (* epoch-days axis-precession-velocity))
      axis)))

(defn cal-position-without-axis-precession [orbit epoch-days]
  (let [{:circle-orbit/keys [start-position axis angular-velocity]} orbit
        position-angle (* angular-velocity epoch-days)
        position (v3/apply-axis-angle
                  (v3/from-seq start-position)
                  (v3/normalize (v3/from-seq axis))
                  position-angle)]
    position))

(defn cal-position-with-axis-precession [orbit epoch-days]
  (let [{:circle-orbit/keys [start-position axis axis-precession-center
                             axis-precession-velocity draconitic-angular-velocity]} orbit
        position-angle (* draconitic-angular-velocity epoch-days)
        position (v3/apply-axis-angle
                  (v3/from-seq start-position)
                  (v3/normalize (v3/from-seq axis))
                  position-angle)]
    (v3/apply-axis-angle position
                         (v3/from-seq axis-precession-center)
                         (* epoch-days axis-precession-velocity))))

(defn cal-position [orbit epoch-days]
  (let [{:circle-orbit/keys [axis-precession-center]} orbit]
    (if axis-precession-center
      (cal-position-with-axis-precession orbit epoch-days)
      (cal-position-without-axis-precession orbit epoch-days))))

(defn cal-tilt-quaternion [circle-orbit]
  (q/from-unit-vectors
   (v3/vector3 0 1 0)
   (v3/normalize (v3/from-seq (:circle-orbit/axis circle-orbit)))))


(comment
  (cal-position sample1 365)
  (cal-tilt-quaternion sample1)

  (cal-current-axis moon-sample1 3834.5007428703702)
  ;; => #object[Vector3 [-0.41329422719604925 0.9063546235612364 -0.08780192546629355]]

  (cal-position moon-sample1 300)
  ;; => #object[Vector3 [0.12328064730424265 0.16499015609975917 -1.2643342637097288]]

  (cal-position-without-axis-precession moon-sample1 365)
  ;; => #object[Vector3 [-0.9172098158619132 -0.4051743820844884 0.7971956308140475]]

  (cal-position-with-axis-precession moon-sample1 365)
  ;; => #object[Vector3 [-0.9024143502132218 -0.4397705673035338 0.7957456808948292]]


  )