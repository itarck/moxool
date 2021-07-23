(ns astronomy.app.dev-component
  (:require
   [applied-science.js-interop :as j]
   [helix.core :refer [defnc $] :as h]
   [reagent.dom]
   [astronomy.component.celestial-sphere :refer [CelestialSphereComponent LongitudeMarksComponent]]
   ["@react-three/drei" :refer [Box Plane OrbitControls]]
   ["react-three-fiber" :refer [Canvas useFrame extend useThree]]))


(defn demo-page []
  [:> Canvas {:camera {:position [1 3 3]}
              :style {:height "680px"}}
   [:ambientLight {:intensity 0.5}]
   [:> OrbitControls]
   [:gridHelper {:args [100 100] :position [0 0 0]}]
   [:> CelestialSphereComponent {:radius 10
                                 :longitudeColorMap {:default "red"}
                                 :latitudeColorMap {:default "red"}}]
   [:> LongitudeMarksComponent {:radius 10
                                :color "red"}]
   #_[:> Box {:args [1 2 3]
            :on-click (fn [e]
                        (let [inter (j/get-in e [:intersections 0 :point])]
                          (js/console.log "box click" inter)))}
    [:meshStandardMaterial {:color "red"}]]])

(defn update! []
  (reagent.dom/render [demo-page] (.getElementById js/document "app")))

(defn init! []
  (update!))