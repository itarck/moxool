(ns astronomy.objects.galaxy.v
  (:require
   [posh.reagent :as p]
   [methodology.view.gltf :as v.gltf]))


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


(defn GalaxyView [props {:keys [conn] :as env}]
  (let [galaxy @(p/pull conn '[*] (get-in props [:object :db/id]))
        {:galaxy/keys [radius]} galaxy
        {:object/keys [position quaternion]} galaxy
        {:celestial/keys [gltf]} galaxy]
    (when (:object/show? galaxy)
      [:mesh {:scale [radius radius radius]
              :position (or position [0 0 0])
              :quaternion (or quaternion [0 0 0 1])}
       [v.gltf/GltfView gltf env]])))
