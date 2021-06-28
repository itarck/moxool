(ns astronomy.model.user.horizontal-coordinate-tool
  (:require 
   [shu.goog.math :as gmath]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.three.spherical :as sph]))



(comment

  (def sample
    #:horizontal-coordinate-tool{:position [0.02 0.02 0.02]
                                 :longitude-interval 90
                                 :latitude-interval 10
                                 :radius 0.001
                                 :show-latitude? true
                                 :show-longitude? true
                                 :show-horizontal-plane? true
                                 :show-compass? true

                                 :object/scene [:scene/name "solar"]

                                 :tool/name "horizontal-coordinate-tool-1"
                                 :tool/chinese-name "地平坐标系工具"
                                 :tool/icon "/image/pirate/earth.jpg"

                                 :entity/type :horizontal-coordinate-tool})
  
;;   
  )



(defn cal-radius [hct]
  (let [position (:horizontal-coordinate-tool/position hct)]
    (v3/length (v3/from-seq position))))


(defn cal-quaternion [hct]
  (seq (q/from-unit-vectors
        (v3/vector3 0 1 0)
        (v3/normalize (v3/from-seq (:horizontal-coordinate-tool/position hct))))))


(defn cal-spherical-coordinate [hct]
  (let [[x y z] (:horizontal-coordinate-tool/position hct)
        sp (sph/from-cartesian-coords x y z)]
    sp))

(defn cal-phi-rotate-axis [hct]
  (let [pv (v3/from-seq (:horizontal-coordinate-tool/position hct))]
    (v3/normalize (v3/cross (v3/vector3 0 1 0) (v3/normalize pv)))))


(defn cal-quaternion-on-sphere [position]
  (let [hct #:horizontal-coordinate-tool{:position position}
        sp (cal-spherical-coordinate hct)
        q1 (q/from-axis-angle (cal-phi-rotate-axis hct) (:phi sp))
        q2 (q/from-axis-angle (v3/vector3 0 1 0) (:theta sp))]
    (q/multiply q1 q2)))


(comment
  (cal-radius sample)
  (cal-quaternion sample)
  ;; => (0.32505758367186816 0 -0.32505758367186816 0.8880738339771153)

  (cal-quaternion-on-sphere [1 1 1])
  ;; => #object[Quaternion [0.42470820027786693 0.33985114297998736 -0.17591989660616117 0.8204732385702833]]

  )