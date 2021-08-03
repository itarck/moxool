(ns astronomy.view.astronomical-coordinate
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   ["react" :as react :refer [Suspense]]
   ["@react-three/drei" :refer [Html]]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [shu.astronomy.celestial-coordinate :as shu.cc]
   [astronomy.model.const :as m.const]
   [astronomy.model.astronomical-point :as m.apt]
   [methodology.lib.geometry :as v.geo]
   [astronomy.view.satellite :as v.satellite]
   [astronomy.component.celestial-sphere :as c.celestial-sphere]
   [astronomy.component.cross-hair :as c.cross-hair]))


(defn EclipticMarksView [{:keys [radius color]}]
  [:mesh {:quaternion m.const/ecliptic-quaternion}
   (for [i (range 36)]
     ^{:key i}
     [:> Html {:position (v3/from-spherical-coords
                          radius
                          (/ Math/PI 2)
                          (* i (/ 1 36) 2 Math/PI))
               :zIndexRange [0 0]
               :style {:color color
                       :font-size "14px"}}
      [:p (str (* i 10) "°")]])])


(defn EclipticSceneView [{:keys [earth]} env]
  [:<>
   [v.geo/CircleComponent {:center [0 0 0]
                           :radius 500
                           :axis m.const/ecliptic-axis
                           :color "orange"}]
   (let [points (c.celestial-sphere/gen-latitude-points 500 0 36)]
     [:mesh {:quaternion m.const/ecliptic-quaternion}
      [v.geo/PointsComponent {:points points
                              :size 40000
                              :color "orange"}]])
   [v.geo/LineComponent {:points [(v3/from-seq [0 0 0])
                                  (v3/multiply-scalar (v3/from-seq (:object/position earth)) -1)]
                         :color "orange"}]
   [EclipticMarksView {:radius 500
                       :color "orange"}]])


(defn AstronomicalCoordinateView
  [props {:keys [conn service-chan] :as env}]
  (let [ac @(p/pull conn '[*] (get-in props [:object :db/id]))
        {:astronomical-coordinate/keys [radius show-latitude? show-longitude? show-regression-line?
                                        show-latitude-0? show-longitude-0? show-ecliptic? show-lunar-orbit?]} ac
        earth @(p/pull conn '[*] [:planet/name "earth"])
        moon @(p/pull conn '[*] [:satellite/name "moon"])
        clock @(p/pull conn '[*] (-> (:celestial/clock earth) :db/id))
        apt-ids (m.apt/sub-all-ids-by-coordinate conn ac)
        apts (doall (mapv (fn [id] @(p/pull conn '[*] id)) apt-ids))]
    (println "AstronomicalCoordinateView: " (:db/id ac) ", " apt-ids)
    [:mesh {:position (:object/position ac)
            :quaternion (:object/quaternion ac)}
     [:<>
      [:> c.celestial-sphere/CelestialSphereComponent
       {:radius radius
        :onClick (fn [e]
                   (let [point-vector3 (j/get-in e [:intersections 0 :point])
                         point-vec (vec (j/call point-vector3 :toArray))

                         cc (shu.cc/from-vector point-vec)
                         event #:event {:action :user/object-clicked
                                        :detail {:astronomical-coordinate ac
                                                 :clicked-point point-vec
                                                 :celestial-coordinate cc
                                                 :alt-key (j/get-in e [:altKey])
                                                 :meta-key (j/get-in e [:metaKey])}}]
                     (go (>! service-chan event))))
        :color "red"
        ;; :currentPoint current-point
        :longitude-interval 30
        :show-latitude? show-latitude?
        :show-longitude? show-longitude?
        :longitude-color-map {:default "#770000"}
        :latitude-color-map {:default "#770000"}}]

      [:<>
       (for [apt apts]
         ^{:key (:db/id apt)}
         [:> Suspense {:fallback nil}
          [:> c.cross-hair/CrossHairComponent
           {:position (:astronomical-point/point apt)
            :onClick (fn [e] (go (>! service-chan
                                     #:event {:action :user/object-clicked
                                              :detail {:astronomical-point apt
                                                       :meta-key (j/get-in e [:metaKey])}})))}]])]


      (when show-latitude-0?
        [:<>
         [:> c.celestial-sphere/LatitudeComponent {:radius radius
                                                   :latitude 0
                                                   :color "red"}]
         [:> c.celestial-sphere/LongitudeMarksComponent {:radius radius
                                                         :color "red"}]])
      (when show-longitude-0?
        [:<>
         [:> c.celestial-sphere/LongitudeComponent {:radius radius
                                                    :longitude 0
                                                    :color "red"}]])

      (when show-regression-line?
        [:<>
         [:> c.celestial-sphere/LatitudeComponent {:radius radius
                                                   :latitude 23.4
                                                   :color "red"}]
         [:> c.celestial-sphere/LatitudeComponent {:radius radius
                                                   :latitude -23.4
                                                   :color "red"}]])

      (when show-ecliptic?
        [EclipticSceneView {:earth earth} env])

      (when show-lunar-orbit?
        [v.satellite/CelestialOrbitView {:orbit (:celestial/orbit moon)
                                         :celestial moon
                                         :clock clock} env])

      ;; 
      ]]))
