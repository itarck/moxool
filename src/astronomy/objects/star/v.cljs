(ns astronomy.objects.star.v
  (:require
   [applied-science.js-interop :as j]
   [cljs-bean.core :refer [->js]]
   [helix.core :refer [defnc $]]
   [helix.hooks :refer [use-memo]]
   [posh.reagent :as p]
   ["@react-three/drei" :refer [Sphere Plane]]
   ["three" :as three]
   ["react" :as react :refer [useRef Suspense]]
   [methodology.view.gltf :as v.gltf]
   [astronomy.objects.star.m :as star.m]
   [astronomy.objects.planet.v :as planet.v]
   [astronomy.component.gltf :as c.gltf]))


(def sample1
  #:star{:name "sun"
         :color "red"
         :radius 20
         :object/position [0 0 0]
         :object/rotation [0 0 0]})


;; 绑定数据层

(defnc DirectionalSunLight [props]
  (let [{:keys [position intensity shadow-camera-near shadow-camera-far shadow-camera-size]} props
        l (new three/DirectionalLight "white" intensity)]
    (j/apply-in l [:position :set] position)
    (j/assoc! l :castShadow true)
    ;; (j/assoc! l :decay 2)
    (j/assoc-in! l [:shadow :camera :near] shadow-camera-near)
    (j/assoc-in! l [:shadow :camera :far] shadow-camera-far)
    (j/assoc-in! l [:shadow :camera :left] (- shadow-camera-size))
    (j/assoc-in! l [:shadow :camera :right] shadow-camera-size)
    (j/assoc-in! l [:shadow :camera :bottom] (- shadow-camera-size))
    (j/assoc-in! l [:shadow :camera :top] shadow-camera-size)
    ($ :primitive {:object l})))


(defnc PointSunLight [props]
  ($ :pointLight {:position #js [0 0 0]
                  :intensity 8}))

(defn StarView [{:keys [astro-scene] :as props} {:keys [conn] :as env}]
  (let [star @(p/pull conn '[*] (get-in props [:object :db/id]))
        planets (star.m/sub-planets conn star)
        celestial-scale (get-in props [:astro-scene :astro-scene/celestial-scale])
        {:star/keys [color]} star
        {:celestial/keys [gltf radius scale] :or {scale 1}} star
        {:object/keys [position quaternion]} star
        scaled-radius (* radius celestial-scale scale)]
    ;; (println "star view" (:planet/_star star))
    [:mesh {:position position}

     (when (and (= (:star/name star) "sun") (:star/show-light? star))
       [:<>
        ($ DirectionalSunLight {:position #js [0 0 0]
                                :intensity 5
                                :shadow-camera-near 1
                                :shadow-camera-far 100000000
                                :shadow-camera-size 2500})
        #_($ PointSunLight {})])

     (when (:object/show? star)
       (if gltf
         [:mesh {:quaternion (or quaternion [0 0 0 1])}
          [:mesh {:scale [scaled-radius scaled-radius scaled-radius]}
           [v.gltf/GltfView gltf env]]

          #_[:PolarGridHelper {:args #js [1000 4 2 10000 "yellow" "yellow"]}]]

         [:> Sphere {:args [radius 10 10]
                     :position [0 0 0]
                     :quaternion (or quaternion [0 0 0 1])}
          [:meshStandardMaterial {:color color}]]))

     [:<>
      (for [planet planets]
        ^{:key (:db/id planet)}
        [planet.v/PlanetView {:planet planet
                              :astro-scene astro-scene} env])]]))


(defnc StarsSphereComponent [props]
  #_(println "mount stars")
  (let [stars (:stars props)
        star-count (count stars)
        positions (use-memo [star-count]
                            (let [positions #js []]
                              (doseq [star stars]
                                (let [[x y z] (star.m/cal-star-position-vector star)]
                                  (j/push! positions x)
                                  (j/push! positions y)
                                  (j/push! positions z)))
                              (js/Float32Array.  positions)))]
    ($ "points"
       ($ "bufferGeometry"
          ($ "bufferAttribute" {:attach-object #js ["attributes" "position"]
                                :count         star-count
                                :array         positions
                                :item-size     3}))
       ($ "pointsMaterial" {:size             250000000000
                            :size-attenuation true
                            :color            "white"
                            :transparent      true
                            :opacity          1
                            :fog              false}))))


(defn StarsSphereView [_props {:keys [conn]}]
  (let [stars (star.m/sub-all-constellation-stars conn)]
    ($ StarsSphereComponent {:stars stars})))


(defn SavedStarsSphereView [{:keys [position]}]
  ($ :mesh {:position (or (->js position) #js [0 0 0])}
     ($ Suspense {:fallback nil}
        ($ c.gltf/GLTF {:url "models/starsphere.gltf"}))))