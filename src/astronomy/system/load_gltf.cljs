(ns astronomy.system.load-gltf
  (:require
   ["react-three-fiber" :refer [Canvas]]
   ["@react-three/drei" :refer [OrbitControls]]
   [posh.reagent :as p]
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [methodology.view.gltf :as v.gltf]
   [astronomy.view.galaxy :as v.galaxy]
   [astronomy.view.background :as v.background]
   [methodology.view.camera :as v.camera]
   [astronomy.model.core :refer [basic-db]]))


(def radius (* 150000 365 86400))

(defn SceneView [props env]
  (let [{:keys [galaxy]} props
        {:keys [conn]} env
        camera @(p/pull conn '[*] [:camera/name "default"])
        galaxy @(p/pull conn '[*] (:db/id galaxy))]
    (println camera)
    [:<>
     [v.galaxy/GalaxyView galaxy env]
     [v.camera/CameraView camera env]]))



(defn RootView [props env]
  [:> Canvas {:style {:background :black}
              ;; :camera {:far 1000000000000000
              ;;          :position [10 10 10]}
              }
   [SceneView props env]
   [:> OrbitControls]
   [:ambientLight {:intensity 0.5}]
   [:gridHelper {:args [100 10 "gray" "gray"] :position [0 0 0]}]
   [:pointLight {:position [1000 1000 1000]
                 :intensity 10}]])


(derive ::conn :circuit/conn)
(derive ::view :circuit/view)
(derive ::chan :circuit/chan)
(derive ::dom-atom :circuit/atom)


(def config
  {::conn #:conn {:initial-db basic-db}
   ::dom-atom #:atom {}
   ::chan #:chan {}
   ::view #:view {:view-fn RootView
                  :props {:galaxy {:db/id [:galaxy/name "milky way"]}}
                  :env {:conn (ig/ref ::conn)
                        :service-chan (ig/ref ::chan)
                        :dom-atom (ig/ref ::dom-atom)}}})


(defn create-system! []
  (ig/init config))
