(ns methodology.model.object
  (:require
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.three.matrix4 :as m4]))

;; 可放置在场景中的3d物体，对应threejs里的 Object3D


(def sample
  #:object
   {:position [0 0 0]
    :rotation [0 0 0]
    :scale [1 1 1]
    :scene #:scene{:name "solar"}})


(def schema 
  {:object/scene {:db/valueType :db.type/ref
                  :db/cardinality :db.cardinality/one}})



;; transform

(defn cal-matrix [obj]
  (let [{:object/keys [position quaternion]} obj
        mat (m4/compose (v3/from-seq position) (q/from-seq quaternion) (v3/vector3 1 1 1))]
    mat))

(defn cal-invert-matrix [obj]
  (m4/invert (cal-matrix obj)))

