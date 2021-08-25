(ns astronomy.objects.planet.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <!]]
   [posh.reagent :as p]
   [reagent.core :as r]
   [helix.core :refer [$]]
   ["@react-three/drei" :refer [Html]]
   [shu.three.vector3 :as v3]
   [astronomy.model.const :as m.const]
   [astronomy.model.ellipse-orbit :as m.ellipse-orbit]
   [methodology.lib.geometry :as v.geo]
   [methodology.view.gltf :as v.gltf]
   [astronomy.objects.planet.m :as planet]
   [astronomy.objects.satellite.v :as satellite.v]
   [astronomy.component.animate :as a]
   [astronomy.component.celestial-sphere :as c.celestial-sphere]
   [astronomy.objects.ecliptic.m :as ecliptic.m]
   [astronomy.objects.ecliptic.v :as ecliptic.v]))


(def earth-1
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



(defn PlanetPositionLineView [{:keys [planet color]} env]
  (let [merged-color (or color 
                  (get-in planet [:celestial/orbit :orbit/color])
                  "gray")]
    [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                   (v3/from-seq (map #(* 1.003 %) (:object/position planet)))]
                          :color merged-color}]))


(defn PlanetOrbitView [{:keys [orbit color]} env]
  (let [new-color (or color (:orbit/color orbit))]
    (cond
      (= (:orbit/type orbit) :ellipse-orbit)
      [v.geo/LineComponent {:points (m.ellipse-orbit/cal-orbit-points-vectors orbit (* 10 360))
                            :color new-color}]

      :else
      [v.geo/CircleComponent {:center [0 0 0]
                              :radius (:circle-orbit/radius orbit)
                              :axis (:circle-orbit/axis orbit)
                              :color new-color
                              :circle-points (* 360 20)}])))


(defn PlanetSpinPlaneView [{:keys [size]} env]
  [:<>
   [:gridHelper {:args [size 10 "gray gray"]}]
   [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                  (v3/from-seq [(* 0.7 size) 0 0])]
                         :color "red"}]])


(defn PlanetPositionLogView [{:keys [planet]} {:keys [conn]}]
  (let [planet-1 @(p/pull conn '[*] (:db/id planet))
        color (get-in planet-1 [:celestial/orbit :orbit/color])]
    [v.geo/LineComponent {:points (mapv (fn [[_epoch-days position]] (v3/from-seq position)) (:planet/position-log planet-1))
                          :color color}]))


(defn PlanetsHasPositionLogView [props {:keys [conn] :as env}]
  (let [planet-ids @(p/q planet/query-all-ids-with-tracker conn)]
    [:<>
     (for [id planet-ids]
       ^{:key id}
       [PlanetPositionLogView {:planet {:db/id id}} env])]))


(defn EpicycleView [props {:keys [conn]}]
  (let [earth @(p/pull conn '[:object/position] [:planet/name "earth"])]
    [:<>
     [v.geo/CircleComponent {:center [0 0 0]
                             :radius 500
                             :axis m.const/ecliptic-axis
                             :color "#606060"}]
     (let [points (c.celestial-sphere/gen-latitude-points 500 0 36)]
       [:mesh {:quaternion m.const/ecliptic-quaternion}
        [v.geo/PointsComponent {:points points
                                :size 40000
                                :color "#606060"}]])
     [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                    (v3/multiply-scalar (v3/from-seq (:object/position earth)) -1)]
                           :color "#606060"}]]))


(defn DeferentEpicycleView [{:keys [planet]} {:keys [conn] :as env}]
  (let [earth @(p/pull conn '[*] [:planet/name "earth"])
        planet-1 @(p/pull conn '[* {:celestial/orbit [*]}] (:db/id planet))
        {:celestial/keys [orbit]} planet-1]
    (when-not (= (:db/id earth) (:db/id planet-1))
      [:mesh {:position (:object/position earth)}
       [PlanetOrbitView {:orbit orbit :color "#606060"} env]
       [PlanetPositionLineView {:planet planet-1 :color "#606060"} env]
       [:mesh {:position (:object/position planet-1)}
        [EpicycleView {} env]]])))



(defn PlanetCelestialView 
  "只有星球部分"
  [{:keys [planet astro-scene] :as props} {:keys [conn service-chan] :as env}]
  (let [planet @(p/pull conn '[{:satellite/_planet [:db/id]
                                :celestial/orbit [*]
                                :celestial/spin [*]} *] (:db/id planet))
        {:object/keys [position quaternion]} planet
        {:celestial/keys [gltf radius spin]} planet
        scaled-radius (* radius (:astro-scene/celestial-scale astro-scene))]
    [:mesh {:position position
            :quaternion quaternion}
     [:mesh {:scale [scaled-radius scaled-radius scaled-radius]
             :onClick (fn [e]
                        (let [pt (j/get-in e [:intersections 0 :point])
                              point (seq (j/call pt :toArray))]
                          (println "click-point: " point)
                          (go (>! service-chan #:event {:action :user/object-clicked
                                                        :detail {:click-point point
                                                                 :alt-key (j/get-in e [:altKey])
                                                                 :meta-key (j/get-in e [:metaKey])
                                                                 :shift-key (j/get-in e [:shiftKey])
                                                                 :object planet}}))))}
      [v.gltf/GltfView gltf env]]

     (when (:spin/show-helper? spin)
       [PlanetSpinPlaneView {:size (* 4 scaled-radius)} env])]))


(defn update-mesh [conn id mesh]
  (let [object @(p/pull conn '[:object/quaternion :object/position] id)
        [qx qy qz qw] (:object/quaternion object)
        [px py pz] (:object/position object)]
    (doto mesh
      (j/assoc-in! [:current :quaternion :x] qx)
      (j/assoc-in! [:current :quaternion :y] qy)
      (j/assoc-in! [:current :quaternion :z] qz)
      (j/assoc-in! [:current :quaternion :w] qw)
      (j/assoc-in! [:current :position :x] px)
      (j/assoc-in! [:current :position :y] py)
      (j/assoc-in! [:current :position :z] pz))))


(defn AnimatedPlanetCelestialView
  "只有星球部分，可用"
  [{:keys [planet astro-scene] :as props} {:keys [conn service-chan] :as env}]
  (let [planet-1 @(p/pull conn '[{:satellite/_planet [:db/id]
                                  :celestial/orbit [*]
                                  :celestial/spin [*]}
                                 :celestial/gltf
                                 :celestial/radius
                                 :db/id]
                          (:db/id planet))
        {:celestial/keys [gltf radius spin]} planet-1
        scaled-radius (* radius (:astro-scene/celestial-scale astro-scene))]
    ($ a/AnimatedMeshComponent {:use-frame-fn (partial update-mesh conn (:db/id planet-1))}
       (r/as-element [:mesh {:position [0 0 0]
                             :quaternion [0 0 0 1]
                             :scale [scaled-radius scaled-radius scaled-radius]
                             :onClick (fn [e]
                                        (let [pt (j/get-in e [:intersections 0 :point])
                                              point (seq (j/call pt :toArray))]
                                          (println "click-point: " point)
                                          (go (>! service-chan #:event {:action :user/object-clicked
                                                                        :detail {:click-point point
                                                                                 :alt-key (j/get-in e [:altKey])
                                                                                 :meta-key (j/get-in e [:metaKey])
                                                                                 :shift-key (j/get-in e [:shiftKey])
                                                                                 :object planet}}))))}
                      [v.gltf/GltfView gltf env]]))))


(defn PlanetView 
  [{:keys [planet astro-scene] :as props} {:keys [conn] :as env}]
  (let [planet @(p/pull conn '[{:satellite/_planet [:db/id]
                                :celestial/orbit [*]
                                :celestial/spin [*]} *] (:db/id planet))
        ;; center-entity (m.astro-scene/sub-scene-center-entity conn astro-scene)
        {:object/keys [position]} planet
        {:celestial/keys [gltf orbit]} planet
        {:planet/keys [show-name? chinese-name show-epicycle?]} planet
        satellites (:satellite/_planet planet)]

    [:<>
     (when (and (:object/show? planet) gltf)
       [PlanetCelestialView props env]
       #_(if (= (:db/id planet) (:db/id center-entity))
         [PlanetCelestialView props env]
         [AnimatedPlanetCelestialView props env]))

     (when show-epicycle?
       [DeferentEpicycleView {:planet planet} env])

     [:mesh {:position position}

      (when show-name?
        [:> Html {:zIndexRange [0 0]}
         [:p {:style {:margin-top "5px"
                      :margin-left "5px"
                      :color "#777"}}
          chinese-name]])

      [:<>
       (for [satellite satellites]
         ^{:key (:db/id satellite)}
         [satellite.v/SatelliteView {:satellite satellite
                                     :astro-scene astro-scene} env])]]

     (when (:orbit/show? orbit) [PlanetOrbitView {:orbit orbit} env])
     (when (:orbit/show? orbit) [PlanetPositionLineView {:planet planet} env])
     
     ]))


