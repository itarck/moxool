(ns astronomy.view.horizontal-coordinate
  (:require
   [posh.reagent :as p]
   [helix.core :refer [defnc $] :as h]
   ["react" :as react :refer [Suspense]]
   ["@react-three/drei" :refer [Cylinder useTexture]]
   [astronomy.model.horizontal-coordinate :as m.horizon]

   [astronomy.view.celestial-sphere-helper :as v.celestial-sphere]))


(defnc CompassComponent [props]
  (let [texture2 (useTexture "/image/moxool/compass.jpg")]
    ($ Cylinder {:args #js [0.000006 0.000006 0.0000005 50]
                 :position #js [0 -0.0000004 0]}
       ($ :meshBasicMaterial {:map texture2 :attach "material"}))))


(defn HorizontalCoordinateSceneView
  [props {:keys [conn] :as env}]
  (let [hc @(p/pull conn '[*] (get-in props [:object :db/id]))
        _ (println hc)
        {:horizontal-coordinate/keys [radius show-latitude? show-longitude? show-horizontal-plane? show-compass? position]} hc
        astro-scene @(p/pull conn '[*] (get-in props [:astro-scene :db/id]))
        coordinate @(p/pull conn '[*] (get-in astro-scene [:astro-scene/coordinate :db/id]))
        earth @(p/pull conn '[*] [:planet/name "earth"])]
    (println "Horizontal Coordinate view:  " (= (:db/id earth) (get-in coordinate [:coordinate/track-position :db/id])))
    (println "Horizontal Coordinate view:  " (vec (m.horizon/cal-quaternion-on-sphere position)) position)
    (when (= (:db/id earth) (get-in coordinate [:coordinate/track-position :db/id]))
      (let [q2 (vec (m.horizon/cal-quaternion-on-sphere position))]
        [:mesh {:position (:object/position earth)}
         [:mesh {:quaternion (:object/quaternion earth)}
          [:mesh {:quaternion q2
                  :position position}
           [:<>
            (when show-horizontal-plane?
              [:polarGridHelper {:args [radius 4 10 60 "green" "green"]}])

            [v.celestial-sphere/CelestialSphereHelperView {:radius radius
                                                           :show-latitude? show-latitude?
                                                           :show-longitude? show-longitude?
                                                           :longitude-interval 90
                                                           :longitude-color-map {:default "#060"
                                                                                 -180 "#0b0"}
                                                           :latitude-color-map {:default "#060"}}]
            (when show-compass?
              ($ Suspense {:fallback nil}
                 ($ CompassComponent)))

            #_[:> Cylinder {:args [0.0000001 0.0000006 0.00001 4]
                            :castShadow true}
               [:meshStandardMaterial {:color "black"}]]]]]]))))
