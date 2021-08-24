(ns astronomy.objects.satellite.v
  (:require
   [applied-science.js-interop :as j]
   ["@react-three/drei" :refer [Sphere]]
   [cljs.core.async :refer [go >! <!]]
   [posh.reagent :as p]
   [methodology.view.gltf :as v.gltf]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.objects.moon-orbit.v :as moon-orbit.v]
   [astronomy.objects.circle-orbit.v :as circle-orbit.v]
   [astronomy.objects.ellipse-orbit.v :as ellipse-orbit.v]))


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


(defn MultiOrbitView
  [{:keys [orbit] :as props} env]
  (case (:orbit/type orbit)
    :moon-orbit [moon-orbit.v/MoonOrbitView props env]
    :ellipse-orbit [ellipse-orbit.v/EllipseOrbitView props env]
    :circle-orbit [circle-orbit.v/CircleOrbitView props env]))


(defn SatelliteView [{:keys [satellite astro-scene] :as props} {:keys [conn service-chan] :as env}]
  (let [satellite @(p/pull conn '[{:celestial/orbit [*]
                                   :celestial/spin [*]}
                                  *] (:db/id satellite))
        {:satellite/keys [color]} satellite
        {:object/keys [position quaternion]} satellite
        {:celestial/keys [orbit gltf radius spin]} satellite
        scaled-radius (* radius (:astro-scene/celestial-scale astro-scene))
        has-day-light? (m.astro-scene/sub-has-day-light? conn astro-scene)]
    ;; (println "satellite view " satellite)
    [:<>
     [:mesh {:position position}
      (when (and (:object/show? satellite) (not has-day-light?))

        [:mesh {:quaternion quaternion}
         (if gltf
           [:mesh {:scale [scaled-radius scaled-radius scaled-radius]
                   :onClick (fn [e]
                              (let [pt (j/get-in e [:intersections 0 :point])
                                    point (seq (j/call pt :toArray))]
                                (go (>! service-chan #:event {:action :user/object-clicked
                                                              :detail {:click-point point
                                                                       :alt-key (j/get-in e [:altKey])
                                                                       :meta-key (j/get-in e [:metaKey])
                                                                       :shift-key (j/get-in e [:shiftKey])
                                                                       :object satellite}}))))}
            [v.gltf/GltfView gltf env]]
           [:> Sphere {:args [radius 10 10]
                       :position [0 0 0]
                       :quaternion quaternion}
            [:meshStandardMaterial {:color color}]])
         (when (:spin/show-helper? spin)
           [:gridHelper {:args [(* 4 scaled-radius) 10 "gray gray"]}])])]

     (when (:orbit/show? orbit) [MultiOrbitView {:celestial satellite
                                                 :orbit orbit
                                                 :clock (:celestial/clock satellite)} env])]))

