(ns methodology.model.object)

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

