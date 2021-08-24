(ns astronomy.objects.horizon-coordinate.v
  (:require
   [posh.reagent :as p]
   [helix.core :refer [defnc $] :as h]
   ["react" :as react :refer [Suspense]]
   ["@react-three/drei" :refer [Cylinder useTexture]]
   [astronomy.component.celestial-sphere :as c.celestial-sphere]))


(defnc CompassComponent [props]
  (let [texture2 (useTexture "/image/moxool/compass.jpg")]
    ($ Cylinder {:args #js [0.0000006 0.0000006 0.0000002 50]
                 :position #js [0 -0.0000002 0]}
       ($ :meshBasicMaterial {:map texture2 :attach "material"}))))


(defn HorizonCoordinateView
  [props {:keys [conn] :as env}]
  (let [hc @(p/pull conn '[*] (get-in props [:object :db/id]))
        {:horizon-coordinate/keys [radius show-latitude? show-longitude? show-horizontal-plane? show-compass?]} hc
        {:object/keys [position quaternion]} hc]
    [:mesh {:quaternion quaternion
            :position position}
     [:<>
      (when show-horizontal-plane?
        [:polarGridHelper {:args [radius 4 10 60 "green" "green"]}])

      [:> c.celestial-sphere/CelestialSphereComponent {:radius radius
                                                       :show-latitude? show-latitude?
                                                       :show-longitude? show-longitude?
                                                       :longitude-interval 90
                                                       :longitude-color-map {:default "#060"
                                                                             -180 "#0b0"}
                                                       :latitude-color-map {:default "#060"}}]
      (when show-compass?
        ($ Suspense {:fallback nil}
           ($ CompassComponent)))]]))

