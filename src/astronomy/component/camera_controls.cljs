(ns astronomy.component.camera-controls
  (:require
   [applied-science.js-interop :as j]
   [cljs-bean.core :refer [bean ->clj ->js]]
   [helix.core :refer [defnc $]]
   ["three" :as three]
   ["react" :as react :refer [useRef useEffect]]
   ["camera-controls" :as CameraControls]
   ["react-three-fiber" :refer [useFrame extend useThree]]))

(extend #js {:CameraControls CameraControls})

(defnc CameraControlsComponent [props]
  (let [{:keys [position up target minDistance maxDistance zoom domAtom]} props
        [px py pz] (seq position)
        [tx ty tz] (seq target)
        [ux uy uz] (seq up)
        {:keys [gl camera]} (bean (useThree))
        ref (useRef)]
    (j/call CameraControls :install #js {:THREE three})
    (useFrame (fn [state delta]
                (when (j/get ref :current)
                  (j/call-in ref [:current :update] delta))
                #_(js/console.log "use frame: " (j/get-in state [:camera :position]))))
    (useEffect (fn []
                 (swap! domAtom assoc :spaceship-camera-control (j/get ref :current))
                 (j/call-in camera [:up :set] ux uy uz)
                 (j/call-in ref [:current :updateCameraUp])
                 (j/call-in ref [:current :setLookAt] px py pz tx ty tz true)
                 (j/call-in ref [:current :zoomTo] (or zoom 1) true)
                 (println "use effect: " )))
    ($ :cameraControls {:ref ref
                        :args #js [camera (j/get gl :domElement)]
                        :minDistance minDistance
                        :maxDistance (or maxDistance js/Infinity)})))