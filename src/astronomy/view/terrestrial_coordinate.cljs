(ns astronomy.view.terrestrial-coordinate
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   [astronomy.view.celestial-sphere-helper :as v.celestial-sphere]))


(defn TerrestrialCoordinateView
  [props {:keys [conn] :as env}]
  (let [tc @(p/pull conn '[*] (get-in props [:object :db/id]))
        {:terrestrial-coordinate/keys [radius show-latitude? show-longitude? show-regression-line?
                                       show-latitude-0? show-longitude-0?]} tc]
    [:mesh {:position (:object/position tc)
            :quaternion (:object/quaternion tc)}
     [:<>
      [v.celestial-sphere/CelestialSphereHelperView {:radius radius
                                                     :longitude-interval 30
                                                     :show-latitude? show-latitude?
                                                     :show-longitude? show-longitude?
                                                     :longitude-color-map {:default "DeepSkyBlue"}
                                                     :latitude-color-map {:default "DeepSkyBlue"}}]

      (when show-latitude-0?
        [:<>
         [v.celestial-sphere/LatitudeView {:radius radius
                                           :latitude 0
                                           :color "blue"}]
         #_[v.celestial-sphere/LongitudeMarksView {:radius radius
                                                 :color "blue"}]])
      (when show-longitude-0?
        [:<>
         [v.celestial-sphere/LongitudeView {:radius radius
                                            :longitude 0
                                            :color "blue"}]])

      (when show-regression-line?
        [:<>
         [v.celestial-sphere/LatitudeView {:radius radius
                                           :latitude 23.4
                                           :color "blue"}]
         [v.celestial-sphere/LatitudeView {:radius radius
                                           :latitude -23.4
                                           :color "blue"}]])


      ;; 
      ]]))
