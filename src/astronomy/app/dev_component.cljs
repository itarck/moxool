(ns astronomy.app.dev-component
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   [cljs-bean.core :refer [bean ->clj ->js]]
   [helix.core :refer [defnc $]]
   [posh.reagent :as p]
   [reagent.dom]
   ["react" :as react :refer [useRef useEffect]]
   ["three" :as three]
   ["camera-controls" :as CameraControls]
   ["@react-three/drei" :refer [Box Plane OrbitControls]]
   ["react-three-fiber" :refer [Canvas useFrame extend useThree]]))

(extend #js {:CameraControls CameraControls})

(defnc CameraControlsComponent [props]
  (let [{:keys [position up target minDistance maxDistance]} props
        [px py pz] (seq position)
        [tx ty tz] (seq target)
        [ux uy uz] (seq up)
        {:keys [gl camera]} (bean (useThree))
        ref (useRef)]
    (j/call CameraControls :install #js {:THREE three})
    (useFrame (fn [_state delta]
                (when (j/get ref :current)
                  (j/call-in ref [:current :update] delta))))
    (useEffect (fn []
                 (j/call-in camera [:up :set] ux uy uz)
                 (j/call-in ref [:current :updateCameraUp])
                 (j/call-in ref [:current :setLookAt] px py pz tx ty tz true)))
    ($ :cameraControls {:ref ref
                        :args #js [camera (j/get gl :domElement)]
                        :truckSpeed 1e-10
                        :minDistance minDistance
                        :maxDistance (or maxDistance js/Infinity)})))


(defn canvas-page []
  [:> Canvas {:camera {:position [1 3 3]}
              :pixelRatio (j/get js/window :devicePixelRatio)
              :style {:height "100%"
                      :width "100%"}}
   [:ambientLight {:intensity 0.5}]
   ($ CameraControlsComponent {:position [10 10 10]
                               :up [0 1 0]
                               :target [0 0 0]
                               :minDistance 0
                               :maxDistance 100000})
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