(ns astronomy.view.terrestrial-coordinate
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   [astronomy.component.celestial-sphere :as c.celestial-sphere]))


(defn TerrestrialCoordinateView
  [props {:keys [conn] :as env}]
  (let [tc @(p/pull conn '[*] (get-in props [:object :db/id]))
        {:terrestrial-coordinate/keys [radius show-latitude? show-longitude? show-regression-line?
                                       show-latitude-0? show-longitude-0?]} tc]
    [:mesh {:position (:object/position tc)
            :quaternion (:object/quaternion tc)}
     [:<>
      [:> c.celestial-sphere/CelestialSphereComponent {:radius radius
                                                       :longitude-interval 30
                                                       :show-latitude? show-latitude?
                                                       :show-longitude? show-longitude?
                                                       :longitude-color-map {:default "DeepSkyBlue"}
                                                       :latitude-color-map {:default "DeepSkyBlue"}}]

      (when show-latitude-0?
        [:<>
         [:> c.celestial-sphere/LatitudeComponent {:radius radius
                                                   :latitude 0
                                                   :color "blue"}]])
      (when show-longitude-0?
        [:<>
         [:> c.celestial-sphere/LongitudeComponent {:radius radius
                                                 :longitude 0
                                                 :color "blue"}]])

      (when show-regression-line?
        [:<>
         [:> c.celestial-sphere/LatitudeComponent {:radius radius
                                                   :latitude 23.4
                                                   :color "blue"}]
         [:> c.celestial-sphere/LatitudeComponent {:radius radius
                                                   :latitude -23.4
                                                   :color "blue"}]])

      ;; 
      ]]))
