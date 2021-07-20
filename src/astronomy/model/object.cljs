(ns astronomy.model.object
  (:require
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.three.matrix4 :as m4]))


;; 3d scene内的物体，有position和quaternion两个表示位置和朝向的参数

(comment 
  
  (def sample 
    #:object {:position [0 0 1]
              :quaternion [0 0 0 1]})
  )


;; transform

(defn cal-matrix [obj]
  (let [{:object/keys [position quaternion]} obj
        mat (m4/compose (v3/from-seq position) (q/from-seq quaternion) (v3/vector3 1 1 1))]
    mat))

(defn cal-invert-matrix [obj]
  (m4/invert (cal-matrix obj)))

