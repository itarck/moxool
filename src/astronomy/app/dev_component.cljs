(ns astronomy.app.dev-component
  (:require
   [applied-science.js-interop :as j]
   [helix.core :refer [defnc $] :as h]
   [reagent.core :as r]
   [reagent.dom]
   [astronomy.component.celestial-sphere :refer [CelestialSphereComponent LongitudeMarksComponent]]
   ["@react-three/drei" :refer [Box Plane OrbitControls]]
   ["react-three-fiber" :refer [Canvas useFrame extend useThree]]))


(defonce db-ref (r/atom {:current-point nil
                         :points []}))


(defn demo-page []
  [:> Canvas {:camera {:position [1 3 3]
                       :far 200000}
              :style {:height "680px"}}
   [:ambientLight {:intensity 0.5}]
   [:> OrbitControls]
   #_[:gridHelper {:args [100 100] :position [0 0 0]}]
   [:> CelestialSphereComponent {:radius 1000
                                 :longitudeColorMap {:default "red"}
                                 :latitudeColorMap {:default "red"}
                                 :color "red"
                                 :currentPoint (:current-point @db-ref)
                                 :points (:points @db-ref)
                                 :onClick (fn [e]
                                            (let [point-vector (j/get-in e [:intersections 0 :point])
                                                  point-seq (seq (j/call point-vector :toArray))
                                                  alt-key (j/get-in e [:altKey])
                                                  meta-key (j/get-in e [:metaKey])]
                                              (when meta-key
                                                (swap! db-ref assoc :current-point (vec point-seq)))
                                              (when alt-key
                                                (swap! db-ref update :points conj (vec point-seq)))
                                              #_(println db-ref)))}]
   [:> LongitudeMarksComponent {:radius 2000
                                :color "red"}]])

(defn update! []
  (reagent.dom/render [demo-page] (.getElementById js/document "app")))

(defn init! []
  (update!))



