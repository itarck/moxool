(ns astronomy.app.dev-component
  (:require
   [applied-science.js-interop :as j]
   [helix.core :refer [defnc $] :as h]
   [cljs-bean.core :refer [bean ->clj ->js]]
   [reagent.core :as r]
   [reagent.dom]
   [astronomy.component.camera-controls :as camera-controls]
   [astronomy.component.celestial-sphere :refer [CelestialSphereComponent LongitudeMarksComponent]]
   ["@material-ui/core" :as mt]
   ["@react-three/drei" :refer [Box Plane OrbitControls]]
   ["react-three-fiber" :refer [Canvas useFrame extend useThree]]))


(defonce db-ref (r/atom {:current-point nil
                         :points []
                         :camera-controls {:azimuthRotateSpeed -0.3
                                           :polarRotateSpeed -0.3
                                           :up [0 1 0]
                                           :target [0 0 0]
                                           :position [100 100 100]
                                           :minDistance 1
                                           :maxDistance 10000
                                           :zoom 1}}))

(def domAtom (atom {}))

(def props
  {:azimuthRotateSpeed -0.3
   :polarRotateSpeed -0.3
   :domAtom domAtom
   :up [0 1 0]
   :target [0 0 0]
   :position [100 100 100]
   :minDistance 1
   :maxDistance 10000
   :zoom 1})



(defnc MyTabs [{:keys [classes]}]
  {:wrap [((mt/withStyles (->js {:paper {:maxWidth 3000 
                                         :minWidth 0
                                         :width 300}
                                 :tabs {:width 300
                                        :minWidth 0}
                                 :tab {:width 100
                                       :minWidth 0}})))]}
  ($ mt/Paper {:className (j/get classes :paper)}
     ($ mt/Tabs {:value 1
                 :indicatorColor "primary"
                 :textColor "primary"
                 :className (j/get classes :tabs)}
        ($ mt/Tab {:label "一" :className (j/get classes :tab)})
        ($ mt/Tab {:label "二" :className (j/get classes :tab)})
        ($ mt/Tab {:label "三" :className (j/get classes :tab)}))))
  


(defn canvas-panel [db-ref domAtom]
  (let [camera-controls (:camera-controls @db-ref)]
    [:> Canvas {:camera {:position [1 3 3]
                         :far 200000}
                :style {:height "680px"}}
     [:ambientLight {:intensity 0.5}]
     [:> camera-controls/CameraControlsComponent (assoc camera-controls :domAtom domAtom)]
     [:gridHelper {:args [100 100] :position [0 0 0]}]
     [:> CelestialSphereComponent
      {:radius 10
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
                      (swap! db-ref update :points conj (vec point-seq)))))}]
     [:> LongitudeMarksComponent {:radius 10
                                  :color "red"}]]))


(defn tool-panel [db-ref domAtom]
  [:div {:style {:position "absolute"
                 :right 0
                 :top 0
                 :width "400px"
                 :height "200px"}}
   [:input {:type :button
            :value "load position"
            :on-click (fn [e]
                        (let [scc-instance (:spaceship-camera-control @domAtom)
                              camera-instance (j/get scc-instance :_camera)
                              position (vec (j/get-in camera-instance [:position]))]
                          (swap! db-ref assoc-in [:camera-controls :position] position)
                          (println db-ref)))}]
   [:input {:type :button
            :value "random position"
            :on-click (fn [e]
                        (swap! db-ref assoc-in [:camera-controls :position] [(rand-int 100) (rand-int 100) (rand-int 100)])
                        (println db-ref))}]
   [:input {:type :button
            :value "stick mode"
            :on-click (fn [e]
                        (let [scc-instance (:spaceship-camera-control @domAtom)
                              camera-instance (j/get scc-instance :_camera)
                              position (vec (j/get-in camera-instance [:position]))
                              props {:azimuthRotateSpeed -0.3
                                     :polarRotateSpeed -0.3
                                     :up [0 1 0]
                                     :target position
                                     :position (map (fn [d] (* d 1.001)) position)
                                     :minDistance 1e-6
                                     :maxDistance 1e-6
                                     :zoom 1}]
                          (swap! db-ref assoc-in [:camera-controls] props)
                          (println db-ref)))}]
   [:input {:type :button
            :value "orbit mode"
            :on-click (fn [e]
                        (let [scc-instance (:spaceship-camera-control @domAtom)
                              camera-instance (j/get scc-instance :_camera)
                              position (vec (j/get-in camera-instance [:position]))
                              props {:azimuthRotateSpeed -0.3
                                     :polarRotateSpeed -0.3
                                     :up [0 1 0]
                                     :target [0 0 0]
                                     :position position
                                     :minDistance 1e-6
                                     :maxDistance 100000
                                     :zoom 1}]
                          (swap! db-ref assoc-in [:camera-controls] props)
                          (println db-ref)))}]
   
   ($ MyTabs)
   
   ])


(defn demo-page []
  [:<>
   [canvas-panel db-ref domAtom]
   [tool-panel db-ref domAtom]])


(defn html-page []
  [:div {:style {:width "300px"}}
   ($ MyTabs)])


(defn update! []
  (reagent.dom/render
   [demo-page] 
  ;;  [html-page]
   (.getElementById js/document "app")))

(defn init! []
  (update!))



