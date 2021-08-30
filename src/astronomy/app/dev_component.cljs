(ns astronomy.app.dev-component
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   [cljs-bean.core :refer [bean ->clj ->js]]
   [datascript.core :as d]
   [helix.core :refer [defnc $]]
   [helix.hooks :refer [use-memo]]
   [posh.reagent :as p]
   [reagent.dom]
   ["react" :as react :refer [useRef useEffect Suspense]]
   ["three" :as three]
   ["camera-controls" :as CameraControls]
   ["@react-three/drei" :refer [Box Plane OrbitControls useGLTF]]
   ["react-three-fiber" :refer [Canvas useFrame extend useThree]]
   [astronomy.scripts.test-conn :refer [test-db11]]))


(extend #js {:CameraControls CameraControls})


(defnc Model [props]
  (let [gltf (useGLTF (:url props))
        scene (j/get gltf :scene)
        copied-scene (use-memo [scene]
                               (j/call scene :clone))

        ref (useRef)]
    (when (:shadow? props)
      (doseq [o (j/get-in copied-scene [:children])]
        (j/assoc! o :castShadow true)
        (j/assoc! o :receiveShadow true)))
    ($ :primitive {:object copied-scene
                   :ref ref
                   :dispose nil})))


(defn GltfView [props]
  (let [gltf props
        {:gltf/keys [url scale position rotation shadow?]} gltf]
    [:mesh {:scale (or scale [1 1 1])
            :position (or position [0 0 0])
            :rotation (or rotation [0 0 0])
            :castShadow true
            :receiveShadow true}
     [:> Suspense {:fallback nil}
      [:> Model {:url url
                 :id (:db/id gltf)
                 :shadow? shadow?}]]]))


(def earth (d/pull test-db11 '[* {:celestial/gltf [*]}] [:planet/name "earth"]))

(def venus (d/pull test-db11 '[* {:celestial/gltf [*]}] [:planet/name "venus"]))
(def sun (d/pull test-db11 '[* {:celestial/gltf [*]}] [:star/name "sun"]))

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
                      :width "100%"
                      :background "black"}}
   [:ambientLight {:intensity 0.5}]
   ($ CameraControlsComponent {:position [10 10 10]
                               :up [0 1 0]
                               :target [0 0 0]
                               :minDistance 0
                               :maxDistance 100000})
   [:gridHelper {:args [100 100] :position [0 0 0]}]

   [GltfView (assoc (:celestial/gltf earth) :gltf/position [20 0 0])]
   [GltfView (assoc (:celestial/gltf venus) :gltf/position [10 0 0])]
   [GltfView (assoc (:celestial/gltf sun) :gltf/position [0 0 0])]])

(defn update! []
  (reagent.dom/render [canvas-page] (.getElementById js/document "app")))

(defn init! []
  (update!))