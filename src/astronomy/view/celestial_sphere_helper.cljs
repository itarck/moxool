(ns astronomy.view.celestial-sphere-helper
  (:require
   [applied-science.js-interop :as j]
   ["three" :as three]
   [helix.core :refer [defnc $]]
   [shu.goog.math :as gmath]
   [shu.three.vector3 :as v3]))



(defn gen-latitude-points [radius latitude]
  (let [phi (gmath/to-radians (- 90 latitude))
        points #js []]
    (doseq [i (range 0 1.001 0.01)]
      (let [pt (v3/from-spherical-coords
                radius
                phi
                (* i 2 Math/PI))]
        (j/push! points pt)))
    points))


(defn gen-longitude-points [radius longitude]
  (let [points #js []]
    (doseq [i (range -90 90.1)]
      (let [phi (gmath/to-radians (- 90 i))
            pt (v3/from-spherical-coords
                radius
                phi
                (gmath/to-radians longitude))]
        (j/push! points pt)))
    points))

(defn LatitudeView [radius latitude]
  (let [lineGeometry (three/BufferGeometry.)]
    (j/call lineGeometry :setFromPoints (gen-latitude-points radius latitude))
    ($ "line" {:geometry lineGeometry}
       ($ "lineBasicMaterial" {:linewidth 1
                               :color (if (= latitude 0) "red" "gray")
                               :linecap "round"
                               :linejoin "round"}))))


(defn LongitudeView [radius longitude]
  (let [lineGeometry (three/BufferGeometry.)]
    (j/call lineGeometry :setFromPoints (gen-longitude-points radius longitude))
    ($ "line" {:geometry lineGeometry}
       ($ "lineBasicMaterial" {:linewidth 1
                               :color (if (= longitude 0) "red" "gray")
                               :linecap "round"
                               :linejoin "round"}))))

(defn CelestialSphereHelperView [radius]
  (let [latitudes (range -90 90 10)
        longtitudes (range -180 180 15)]
    [:<>
     (for [latitude latitudes]
       ^{:key (str "latitude" latitude)}
       [LatitudeView radius latitude])
     (for [longtitude longtitudes]
       ^{:key (str "longitude" longtitude)}
       [LongitudeView radius longtitude])]))
