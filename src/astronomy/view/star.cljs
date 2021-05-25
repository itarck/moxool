(ns astronomy.view.star
  (:require
   ["@react-three/drei" :refer [Sphere]]
   [posh.reagent :as p]

   [methodology.view.gltf :as v.gltf]
   [astronomy.view.planet :as v.planet]))


(def sample1
  #:star{:name "sun"
         :color "red"
         :radius 20
         :object/position [0 0 0]
         :object/rotation [0 0 0]})


;; 绑定数据层

(defn StarView [entity {:keys [conn] :as env}]
  (let [star @(p/pull conn '[* {:planet/_star [:db/id]}] (:db/id entity))
        {:star/keys [color radius]} star
        {:celestial/keys [gltf]} star
        {:object/keys [position quaternion]} star]
    ;; (println "star view" (:planet/_star star))
    [:mesh {:position position}
     (when (= (:star/name star) "sun")
       [:pointLight {:intensity 8}])
     
     (when (:object/show? star)
       (if gltf
         [:mesh {:quaternion (or quaternion [0 0 0 1])}
          [:mesh {:scale [radius radius radius]}
           [v.gltf/GltfView gltf env]]

          #_[:PolarGridHelper {:args #js [1000 12 10 10000 "yellow" "yellow"]}]

        ;; 
          ]

         [:> Sphere {:args [radius 10 10]
                     :position [0 0 0]
                     :quaternion (or quaternion [0 0 0 1])}
          [:meshStandardMaterial {:color color}]]))
     
     
     [:<>
      (for [planet (:planet/_star star)]
        ^{:key (:db/id planet)}
        [v.planet/PlanetView planet env])]]))