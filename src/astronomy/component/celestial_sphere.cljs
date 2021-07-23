(ns astronomy.component.celestial-sphere
  (:require
   [applied-science.js-interop :as j]
   ["three" :as three]
   ["@react-three/drei" :refer [Html]]
   [cljs-bean.core :refer [bean ->clj ->js]]
   [reagent.core :as r]
   [helix.core :as h :refer [defnc $]]
   [shu.goog.math :as gmath]
   [shu.three.vector3 :as v3]))


(defn gen-latitude-points
  ([radius latitude]
   (gen-latitude-points radius latitude 100))
  ([radius latitude number]
   (let [phi (gmath/to-radians (- 90 latitude))
         points #js []]
     (doseq [i (range 0 1.001 (/ 1.0 number))]
       (let [pt (v3/from-spherical-coords
                 radius
                 phi
                 (* i 2 Math/PI))]
         (j/push! points pt)))
     points)))


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

(defnc LatitudeComponent [{:keys [radius latitude color linewidth]}]
  (let [lineGeometry (three/BufferGeometry.)]
    (j/call lineGeometry :setFromPoints (gen-latitude-points radius latitude))
    ($ "line" {:geometry lineGeometry}
       ($ "lineBasicMaterial" {:linewidth (or linewidth 1)
                               :color color
                               :linecap "round"
                               :linejoin "round"}))))

(defnc LongitudeMarksComponent [{:keys [radius color]}]
  (r/as-element
   [:<>
    (for [i (range 24)]
      ^{:key i}
      [:> Html {:position (v3/from-spherical-coords
                           radius
                           (/ Math/PI 2)
                           (* i (/ 1 24) 2 Math/PI))
                :zIndexRange [0 0]
                :style {:color color
                        :font-size "14px"}}
       [:p (str i "h")]])]))


(defnc LongitudeComponent [{:keys [radius longitude color] :as props}]
  (let [lineGeometry (three/BufferGeometry.)]
    (j/call lineGeometry :setFromPoints (gen-longitude-points radius longitude))
    ($ "line" {:geometry lineGeometry}
       ($ "lineBasicMaterial" {:linewidth 1
                               :color color
                               :linecap "round"
                               :linejoin "round"}))))

(defn gen-color [color-map value]
  (or (get color-map value)
      (get color-map :default)))


(defnc CelestialSphereComponent [props]

  (let [default-props {:radius 100
                       :showLongitude? true
                       :longitudeInterval 45
                       :longitudeColorMap #js {:default "#555"}
                       :showLatitude? true
                       :latitudeInterval 10
                       :latitudeColorMap #js {:default "#555"}}
        celes-sphere (merge default-props props)
        latitudes (range -90 90 (:latitudeInterval celes-sphere))
        longitudes (range -180 180 (:longitudeInterval celes-sphere))]
    (h/<>
     (when (:showLatitude? celes-sphere)
       (for [latitude latitudes]
         ($ LatitudeComponent {:key (str "latitude" latitude)
                               :radius (:radius celes-sphere)
                               :latitude latitude
                               :color (gen-color (->clj (:latitudeColorMap celes-sphere)) latitude)
                               :linewidth (if (= latitude 0) 3 1)})))

     (when (:showLongitude? celes-sphere)
       (for [longitude longitudes]
         ($ LongitudeComponent {:key (str "longitude" longitude)
                                :radius (:radius celes-sphere)
                                :longitude longitude
                                :color (gen-color (->clj (:longitudeColorMap celes-sphere)) longitude)}))))))



(comment 
  
  
  
  )

