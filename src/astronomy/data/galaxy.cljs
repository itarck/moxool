(ns astronomy.data.galaxy
  (:require
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [shu.geometry.angle :as shu.angle]
   [shu.astronomy.celestial-coordinate :as shu.cc]
   [shu.astronomy.light :as shu.light]))


;; * 银心：在天球赤道座标系统的座标是：
;; 赤经 17h45m40.04s，赤纬 -29º 00' 28.1"（J2000 分点）。25000光年

;; * 北银极：换算成2000.0历元的坐标，北银极位于赤经12h 51m 26.282s，赤纬+27° 07′ 42.01″（2000.0历元），银经0度的位置角是122.932°.[4]

(def galaxy-center-vector (shu.cc/cal-position (shu.cc/celestial-coordinate
                                                (shu.angle/convert-hours-to-degrees (shu.angle/gen-hours {:hour 17 :minute 45 :second 40.04}))
                                                (shu.angle/gen-degrees {:degree -29 :minute 0 :second -28.1})
                                                (* shu.light/light-year-unit 25000))))
galaxy-center-vector
;; => #object[Vector3 [-688150222274.2922 -382317840299.07935 -43091769201.20502]]

(def galaxy-north-vector
  (shu.cc/to-unit-vector (shu.cc/celestial-coordinate
                          (shu.angle/convert-hours-to-degrees (shu.angle/gen-hours {:hour 12 :minute 51 :second 26.282}))
                          (shu.angle/gen-degrees {:degree 27 :minute 07 :second  42.01}))))

galaxy-north-vector
;; => #object[Vector3 [-0.19807664997748894 0.45598511375759465 -0.8676653829473483]]

(def galaxy-quaternion (q/from-unit-vectors (v3/vector3 0 1 0) galaxy-north-vector))

galaxy-quaternion
;; => #object[Quaternion [-0.5084623562343109 0 0.11607530061927626 0.8532247985606123]]


(def galaxy
  #:galaxy
   {:name "milky way"
    :chinese-name "银河"
    :radius (* 150000 365 86400)
    :celestial/gltf #:gltf{:url "models/13-galaxy/scene.gltf"
                           :scale (-> (v3/from-seq [0.01 0.005 0.01])
                                      (v3/multiply-scalar 0.3)
                                      seq)
                           :position (-> (v3/from-seq [-1.12 -0.57 1.12])
                                         (v3/multiply-scalar 0.3)
                                         seq)}
    :object/position (vec galaxy-center-vector)
    :object/quaternion (vec galaxy-quaternion)
    :object/scene [:scene/name "solar"]
    :object/show? true
    :entity/chinese-name "银河"
    :entity/type :galaxy})


(def dataset1 [galaxy])