(ns astronomy.model.galaxy)


(def schema {:galaxy/name {:db/unique :db.unique/identity}})


(def sample-1
  #:galaxy
   {:name "milky way"
    :chinese-name "银河"
    :radius (* 150000 365 86400)
    :celestial/gltf #:gltf{:url "models/13-galaxy/scene.gltf"
                           :scale [1 0.5 1]
                           :position [-112 -57 112]}
    :object/scene [:scene/name "solar"]
    :entity/type :galaxy})


