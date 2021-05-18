(ns astronomy.view.planet
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <!]]
   ["@react-three/drei" :refer [Sphere]]
   [posh.reagent :as p]

   [astronomy.model.circle-orbit :as m.circle-orbit]
   [astronomy.model.spin :as m.spin]

   [methodology.view.gltf :as v.gltf]
   [astronomy.view.satellite :as v.satellite]))


(def earth
  #:planet {:name "earth"
            :chinese-name "地球"
            :radius 2
            :color "blue"
            :star [:star/name "sun"]

            :celestial/orbit #:circle-orbit {:star [:star/name "sun"]
                                             :start-position [100 0 0]
                                             :axis [-1 2 0]
                                             :period 365}
            :celestial/spin #:spin {:axis [0 1 0]
                                    :period 1}
            :celestial/gltf #:gltf {:url "models/11-tierra/scene.gltf"
                                    :scale [0.2 0.2 0.2]}
            :object/scene [:scene/name "solar"]
            :object/position [100 0 0]
            :entity/type :planet})



;; 绑定数据层

(defn PlanetView [entity {:keys [conn service-chan] :as env}]
  (let [planet @(p/pull conn '[{:satellite/_planet [:db/id]} *] (:db/id entity))
        {:planet/keys [color radius]} planet
        {:object/keys [position quaternion]} planet
        {:celestial/keys [gltf orbit spin]} planet
        q-orbit-tilt (m.circle-orbit/cal-tilt-quaternion orbit)
        ;; q-spin-tilt (m.spin/cal-tilt-quaternion spin)
        satellites (:satellite/_planet planet)]
    ;; (println "planet view: " planet)
    [:group {:position position}
     (if gltf
       [:mesh {:quaternion quaternion}
        [:mesh {:scale [radius radius radius]
                :onClick (fn [e]
                           (let [pt (j/get-in e [:intersections 0 :point])
                                 point (seq (j/call pt :toArray))]
                             (go (>! service-chan #:event {:action :user/object-clicked
                                                           :detail {:click-point point
                                                                    :object planet}}))))}
         [v.gltf/GltfView gltf env]]
        #_[:PolarGridHelper {:args [0.5 8 5 64 "deepskyblue" "deepskyblue"]}]
        ]

       [:> Sphere {:args [radius 10 10]
                   :position [0 0 0]
                   :quaternion quaternion}
        [:gridHelper {:args [0.2 10 "yellow" "yellow"]}]
        [:meshStandardMaterial {:color color}]])
     
     #_[:gridHelper {:args [2 10 "gray" "gray"]
                   :position [0 0 0]
                   :quaternion (vec q-orbit-tilt)}]
     [:<>
      (for [satellite satellites]
        ^{:key (:db/id satellite)}
        [v.satellite/SatelliteView satellite env])]]))