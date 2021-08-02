(ns astronomy.app.box
  (:require
   [applied-science.js-interop :as j]
   [helix.core :refer [defnc $] :as h]
   [reagent.dom]
   ["three" :as three]
    ["camera-controls" :as CameraControls]

   ["@react-three/drei" :refer [Box Plane OrbitControls]]
   ["react-three-fiber" :refer [Canvas useFrame extend useThree]]))


(extend #js {:CameraControls CameraControls})

(defn canvas-page []
  [:> Canvas {:camera {:position [1 3 3]}}
   [:ambientLight {:intensity 0.5}]
   [:> OrbitControls]
   [:gridHelper {:args [100 100] :position [0 0 0]}]
   [:> Box {:on-click (fn [e]
                        (let [inter (j/get-in e [:intersections 0 :point])]
                          (js/console.log "box click" inter)))
            ;; :on-pointer-over #(js/console.log "hover on")
            ;; :on-pointer-out  #(js/console.log "hover out")
            :args [1 2 3]}
    [:meshStandardMaterial {:color "red"
                            :side three/DoubleSide
                            :opacity 0.1
                            :transparent true}]]])

(defn update! []
  (reagent.dom/render [canvas-page] (.getElementById js/document "app")))

(defn init! []
  (update!))