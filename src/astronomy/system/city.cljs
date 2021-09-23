(ns astronomy.system.city
  (:require
   [applied-science.js-interop :as j]
   [helix.core :as helix :refer [defnc $]]
   [helix.hooks :refer [use-memo]]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   ["react" :as react :refer [useRef Suspense]]
   ["react-three-fiber" :refer [Canvas useFrame extend useThree]]
   ["@react-three/drei" :refer [useGLTF OrbitControls FlyControls]]))


;; helix version

(helix/defnc Model [props]
  (let [gltf (useGLTF (:url props))
        scene (j/get gltf :scene)
        copied-scene (use-memo [scene]
                               (j/call scene :clone))]

    ($ :primitive {:object copied-scene
                   :dispose nil})))


(def city-url "models/3-cityscene_kyoto_1995/scene.gltf")

(defnc CityComponent [{:keys [position]}]
  ($ :mesh {:position (or position #js [0 0 0])}
   ($ Suspense {:fallback nil}
    ($ Model {:url city-url}))))

(defn cityscene_kyoto-page []
  [:> Canvas {:camera {:far 10000000
                       :position [1000 1000 1000]}}
   [:ambientLight {:intensity 0.5}]
   [:pointLight {:position [1000 1000 1000]}]
   [:> CityComponent {:position [0 0 100]}]
   [:> CityComponent {:position [3000 0 100]}]

   [:gridHelper {:args [3000 20] :position [0 0 0]}]
   ($ OrbitControls)])


(defn update! []
  (reagent.dom/render [cityscene_kyoto-page]
                      (.getElementById js/document "app")))

(defn init! []
  (update!))
