(ns astronomy.app.gltf-export
  (:require
   [applied-science.js-interop :as j]
   [datascript.core :as d]
   [posh.reagent :as p]
   [helix.core :as h :refer [defnc $]]
   [helix.hooks :refer [use-memo]]
   [cljs-bean.core :refer [->js ->clj]]
   [shu.three.vector3 :as v3]
   [shu.arithmetic.number :as number]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   ["react" :as react :refer [useRef Suspense]]
   ["react-three-fiber" :refer [Canvas useFrame extend useThree]]
   ["three/examples/jsm/exporters/GLTFExporter" :refer [GLTFExporter]]
   ["@react-three/drei" :refer [useGLTF OrbitControls FlyControls]]
   [astronomy.scripts.test-conn :refer [test-db12]]
   [astronomy.view.star :as v.star]
   [astronomy.view.constellation :as v.constel]))



(def conn 
  (let [conn1 (d/conn-from-db test-db12)]
    (p/posh! conn1)
    conn1))


(defonce dom-atom
  (atom {}))

;; helix version
(h/defnc Model [props]
  (let [gltf (useGLTF (:url props))
        scene (j/get gltf :scene)
        copied-scene (use-memo [scene]
                               (j/call scene :clone))]

    ($ :primitive {:object copied-scene
                   :dispose nil})))


(defnc SavedStarsComponent [{:keys [position]}]
  ($ :mesh {:position (or position #js [0 0 0])}
     ($ Suspense {:fallback nil}
        ($ Model {:url "models/starsphere.gltf"}))
     #_($ Suspense {:fallback nil}
        ($ Model {:url "models/constellations.gltf"}))))


(defnc SceneComponent []
  (let [{:keys [scene]} (->clj (useThree))]
    (swap! dom-atom assoc :scene scene)

    #_($ :mesh {:scale #js [10000 10000 10000]}
         (v.star/StarsSphereView {} {:conn conn}))
    
    #_(r/as-element [v.constel/ConstellationsView {} {:conn conn}])
    
    ($ SavedStarsComponent)
    #_(v.star/StarsSphereView {} {:conn conn})
    ))


(defn scene-page []
  [:> Canvas {:camera {:far 100000000000000000
                       :position [1000 1000 1000]}
              :style {:background :black}}
   [:ambientLight {:intensity 0.5}]
   [:pointLight {:position [10 10 10]}]
   [:> SceneComponent]
;;    [:gridHelper {:args [3000 20] :position [0 0 0]}]
   ($ OrbitControls)])



(defn update! []
  (reagent.dom/render [scene-page]
                      (.getElementById js/document "app")))

(defn init! []
  (update!))



(defn download-value [value export-name]
  (let [data-blob (js/Blob. #js [(str value)] #js {:type "text/plain"})
        link (.createElement js/document "a")]
    (set! (.-href link) (.createObjectURL js/URL data-blob))
    (.setAttribute link "download" export-name)
    (.appendChild (.-body js/document) link)
    (.click link)
    (.removeChild (.-body js/document) link)))


(defn exportGLTF [scene]
  (let [gltf-export (new GLTFExporter)]
    (j/call gltf-export :parse scene
            (fn [gltf]
              (let [scene-string (j/call js/JSON :stringify gltf nil 2)]
                ;; (js/console.log scene-string)
                (download-value scene-string "starsphere.gltf"))))))



(comment

  (keys @dom-atom)

  (exportGLTF (:scene @dom-atom))
  
  )
