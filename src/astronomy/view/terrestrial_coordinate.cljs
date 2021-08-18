(ns astronomy.view.terrestrial-coordinate
  (:require
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   [astronomy.component.celestial-sphere :as c.celestial-sphere]))


(defn TerrestrialCoordinateView
  [props {:keys [conn] :as env}]
  (let [tc @(p/pull conn '[*] (get-in props [:object :db/id]))
        {:terrestrial-coordinate/keys [radius show-latitude? show-longitude? show-regression-line?
                                       show-latitude-0? show-longitude-0? default-color highlight-color]} tc]
    [:mesh {:position (:object/position tc)
            :quaternion (:object/quaternion tc)}
     [:<>
      [:> c.celestial-sphere/CelestialSphereComponent {:radius radius
                                                       :longitude-interval 30
                                                       :show-latitude? show-latitude?
                                                       :show-longitude? show-longitude?
                                                       :longitude-color-map {:default default-color}
                                                       :latitude-color-map {:default default-color}}]

      (when show-latitude-0?
        [:<>
         [:> c.celestial-sphere/LatitudeComponent {:radius radius
                                                   :latitude 0
                                                   :color highlight-color}]])
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

      ;; 
      ]]))
