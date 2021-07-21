(ns astronomy.view.horizon-coordinate
  (:require
   [posh.reagent :as p]
   [helix.core :refer [defnc $] :as h]
   ["react" :as react :refer [Suspense]]
   ["@react-three/drei" :refer [Cylinder useTexture]]
   [astronomy.model.horizon-coordinate :as m.horizon-coordinate]

   [astronomy.view.celestial-sphere-helper :as v.celestial-sphere]))


(defnc CompassComponent [props]
  (let [texture2 (useTexture "/image/moxool/compass.jpg")]
    ($ Cylinder {:args #js [0.000006 0.000006 0.0000005 50]
                 :position #js [0 -0.0000004 0]}
       ($ :meshBasicMaterial {:map texture2 :attach "material"}))))


(defn HorizonCoordinateView
  [props {:keys [conn] :as env}]
  (let [hc @(p/pull conn '[*] (get-in props [:object :db/id]))
        {:horizon-coordinate/keys [radius show-latitude? show-longitude? show-horizontal-plane? show-compass? position]} hc
        local-position (m.horizon-coordinate/cal-local-position hc)
        local-quaternion (m.horizon-coordinate/cal-local-quaternion hc)
        center-position (:coordinate/center-position hc)
        center-quaternion (:coordinate/center-quaternion hc)]
    ;; (println "HorizonCoordinateView " hc)
    [:mesh {:position center-position}
     [:mesh {:quaternion center-quaternion}
      [:mesh {:quaternion local-quaternion
              :position local-position}
       [:<>
        (when show-horizontal-plane?
          [:polarGridHelper {:args [radius 4 10 60 "green" "green"]}])

        [v.celestial-sphere/CelestialSphereHelperView {:radius radius
                                                       :show-latitude? true
                                                       :show-longitude? true
                                                       :longitude-interval 90
                                                       :longitude-color-map {:default "#060"
                                                                             -180 "#0b0"}
                                                       :latitude-color-map {:default "#060"}}]
        (when show-compass?
          ($ Suspense {:fallback nil}
             ($ CompassComponent)))]]]]))
