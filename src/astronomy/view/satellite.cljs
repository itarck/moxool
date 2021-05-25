(ns astronomy.view.satellite
  (:require
   ["@react-three/drei" :refer [Sphere]]
   [posh.reagent :as p]
   [methodology.view.gltf :as v.gltf]
   [astronomy.model.circle-orbit :as m.circle-orbit]))


(def moon
  #:satellite
   {:name "moon"
    :chinese-name "月球"
    :radius 1
    :color "gray"
    :planet [:planet/name "earth"]

    :celestial/orbit #:circle-orbit {:start-position [0 0 30]
                                     :axis [1 2 0]
                                     :period 30}
    :celestial/spin #:spin {:axis [0 1 0]
                            :period 1}
    :celestial/gltf #:gltf {:url "models/11-tierra/scene.gltf"
                            :scale [0.2 0.2 0.2]}
    :object/scene [:scene/name "solar"]

    :object/position [0 0 30]
    :entity/type :satellite})


;; 绑定数据层

(defn SatelliteView [entity {:keys [conn] :as env}]
  (let [satellite @(p/pull conn '[*] (:db/id entity))
        {:satellite/keys [color radius]} satellite
        {:object/keys [position quaternion]} satellite
        {:celestial/keys [orbit gltf]} satellite
        qt (m.circle-orbit/cal-tilt-quaternion orbit)]
    ;; (println "satellite view " satellite)
    [:mesh {:position position}
     (when (:object/show? satellite)
       (if gltf
         [:mesh
          [:mesh {:scale [radius radius radius]}
           [v.gltf/GltfView gltf env]]
          #_[:PolarGridHelper {:args #js [0.3 8 5 64 "white" "white"]}]]

         [:> Sphere {:args [radius 10 10]
                     :position [0 0 0]
                     :quaternion quaternion}
          [:meshStandardMaterial {:color color}]]))
     

     #_[:gridHelper {:args [1 20 "gray" "gray"]
                   :position [0 0 0]
                   :quaternion (vec qt)}]]
    ))

