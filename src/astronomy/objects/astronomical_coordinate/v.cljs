(ns astronomy.objects.astronomical-coordinate.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   [shu.astronomy.celestial-coordinate :as shu.cc]
   [astronomy.objects.astronomical-point.m :as m.apt]
   [astronomy.component.celestial-sphere :as c.celestial-sphere]
   [astronomy.objects.astronomical-point.v :as v.apt]
   [astronomy.objects.moon-orbit.v :as moon-orbit.v]))



(defn AstronomicalCoordinateView
  [{:keys [astro-scene] :as props} {:keys [conn service-chan] :as env}]
  (let [ac @(p/pull conn '[*] (get-in props [:object :db/id]))
        {:astronomical-coordinate/keys [radius show-latitude? show-longitude? show-regression-line?
                                        show-latitude-0? show-longitude-0? show-lunar-orbit?
                                        default-color highlight-color show-marks?]} ac
        earth @(p/pull conn '[*] [:planet/name "earth"])
        moon @(p/pull conn '[*] [:satellite/name "moon"])
        clock @(p/pull conn '[*] (-> (:celestial/clock earth) :db/id))
        apt-ids (m.apt/sub-all-ids-by-coordinate conn ac)
        apts (doall (mapv (fn [id] @(p/pull conn '[*] id)) apt-ids))]
    
    ;; (println "AstronomicalCoordinateView: " (:db/id ac) ", " apt-ids)
    
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
        :longitude-interval 30
        :show-latitude? show-latitude?
        :show-longitude? show-longitude?
        :longitude-color-map {:default default-color}
        :latitude-color-map {:default default-color}}]

      [:<>
       (for [apt apts]
         ^{:key (:db/id apt)}
         [v.apt/AstronimicalPointSceneView {:astronomical-point apt} env])]


      (when show-latitude-0?
        [:> c.celestial-sphere/LatitudeComponent {:radius radius
                                                  :latitude 0
                                                  :color highlight-color}])

      (when show-marks?
        [:> c.celestial-sphere/LongitudeMarksComponent {:radius radius
                                                        :color highlight-color}])
      
      (when show-longitude-0?
        [:<>
         [:> c.celestial-sphere/LongitudeComponent {:radius radius
                                                    :longitude 0
                                                    :color highlight-color}]])

      (when show-regression-line?
        [:<>
         [:> c.celestial-sphere/LatitudeComponent {:radius radius
                                                   :latitude 23.4
                                                   :color highlight-color}]
         [:> c.celestial-sphere/LatitudeComponent {:radius radius
                                                   :latitude -23.4
                                                   :color highlight-color}]])

      (when show-lunar-orbit?
        [moon-orbit.v/MoonOrbitView {:astro-scene astro-scene
                                     :orbit (:celestial/orbit moon)
                                     :celestial moon
                                     :clock clock} env])

      ;; 
      ]]))
