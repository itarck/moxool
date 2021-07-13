(ns astronomy.view.satellite
  (:require
   [applied-science.js-interop :as j]
   ["@react-three/drei" :refer [Sphere]]
   [cljs.core.async :refer [go >! <!]]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [methodology.view.gltf :as v.gltf]
   [astronomy.model.ellipse-orbit :as m.ellipse-orbit]
   [astronomy.model.moon-orbit :as m.moon-orbit]
   [astronomy.model.circle-orbit :as m.circle-orbit]
   [astronomy.model.astro-scene :as m.astro-scene]
   [methodology.lib.geometry :as v.geo]))


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

(defn CelestialPositionLineView [{:keys [celestial]} env]
  (let [color (or (get-in celestial [:celestial/orbit :orbit/color])
                  "gray")]
    [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                   (v3/from-seq (map #(* 1.003 %) (:object/position celestial)))]
                          :color color}
     ]))



(defn CelestialOrbitView [{:keys [orbit celestial clock]} {:keys [conn] :as env}]
  (cond
    (= (:orbit/type orbit) :moon-orbit)
    (let [clock @(p/pull conn '[*] (:db/id clock))
          epoch-day (:clock/time-in-days clock)
          days (range (+ -15 epoch-day) (+ 15 epoch-day) 0.1)]
      [:<>
       [v.geo/LineComponent {:points (m.moon-orbit/cal-orbit-points-vectors orbit days)
                             :color (:orbit/color orbit)}]
       [CelestialPositionLineView {:celestial celestial} env]
       #_[v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                      (m.moon-orbit/cal-perigee-vector orbit epoch-day)]
                             :color "#444"}]])


    (= (:orbit/type orbit) :ellipse-orbit)
    [v.geo/LineComponent {:points (m.ellipse-orbit/cal-orbit-points-vectors orbit (* 10 360))
                          :color (:orbit/color orbit)}]

    :else
    [v.geo/CircleComponent {:center [0 0 0]
                            :radius (:circle-orbit/radius orbit)
                            :axis (:circle-orbit/axis orbit)
                            :color (:orbit/color orbit)
                            :circle-points (* 360 20)}]))


(defn SatelliteView [{:keys [satellite astro-scene has-day-light?] :as props} {:keys [conn service-chan] :as env}]
  (let [satellite @(p/pull conn '[{:celestial/orbit [*]
                                   :celestial/spin [*]}
                                  *] (:db/id satellite))
        {:satellite/keys [color]} satellite
        {:object/keys [position quaternion]} satellite
        {:celestial/keys [orbit gltf radius spin]} satellite
        scaled-radius (* radius (:astro-scene/celestial-scale astro-scene))]
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

     (when (:orbit/show? orbit) [CelestialOrbitView {:celestial satellite
                                                     :orbit orbit
                                                     :clock (:celestial/clock satellite)} env])
     #_(when (:orbit/show? orbit) [CelestialPositionLineView {:celestial satellite} env])
     
     ]))

