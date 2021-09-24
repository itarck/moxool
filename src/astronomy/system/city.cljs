(ns astronomy.system.city
  (:require
   [applied-science.js-interop :as j]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [helix.core :as helix :refer [defnc $]]
   [helix.hooks :refer [use-memo]]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [astronomy.space.camera.v :as camera.v]
   [astronomy.space.camera.s :as camera.s]
   ["react" :as react :refer [useRef Suspense]]
   ["react-three-fiber" :refer [Canvas useFrame extend useThree]]
   ["@react-three/drei" :refer [useGLTF OrbitControls FlyControls]]))


;; model 

(def schema {:camera/name {:db/unique :db.unique/identity}})

(def initial-tx [#:camera{:name "default"
                          :position [1000 1000 1000]
                          :rotation [0 0 0]
                          :far 20000}])


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

(defn RootPage [props env]
  [:> Canvas 
   [camera.v/CameraView {:db/id [:camera/name "default"]} env]
   [:ambientLight {:intensity 0.5}]
   [:pointLight {:position [1000 1000 1000]}]
   [:> CityComponent {:position [0 0 100]}]

   [:gridHelper {:args [3000 20] :position [0 0 0]}]
   ($ OrbitControls)])



;; ig

(derive ::conn :circuit/conn)
(derive ::chan :circuit/chan)
(derive ::service :circuit/service)
(derive ::view :circuit/view)
(derive ::meta-atom :circuit/atom)  ;; 记录控制整个系统的atom
(derive ::dom-atom :circuit/atom)   ;; 记录dom的状态
(derive ::state-atom :circuit/ratom)   ;; 不同服务间共享一些数据


(def db
  (let [conn (d/create-conn schema)]
    (d/transact! conn initial-tx)
    @conn))


(def ioframe-config-sample
  #:ioframe {:db-transit-str (dt/write-transit-str db)
             :name "city-1"
             :type "city"
             :description "一个城市"})


(defn create-ioframe-system [ioframe-config]
  (let [{:ioframe/keys [db-transit-str]} ioframe-config

        config {::conn #:conn {:db-transit-str db-transit-str}
                ::meta-atom  #:atom {:init-value {:mode :read-and-write}}
                ::state-atom #:ratom {}
                ::dom-atom #:atom {}
                ::chan #:chan {}
                ::service #:service {:service-fn camera.s/init-service!
                                     :props {}
                                     :env {:service-chan (ig/ref ::chan)
                                           :conn (ig/ref ::conn)
                                           :meta-atom (ig/ref ::meta-atom)
                                           :state-atom (ig/ref ::state-atom)
                                           :dom-atom (ig/ref ::dom-atom)}}

                ::view #:view {:view-fn RootPage
                               :props {}
                               :env {:service-chan (ig/ref ::chan)
                                     :conn (ig/ref ::conn)
                                     :meta-atom (ig/ref ::meta-atom)
                                     :state-atom (ig/ref ::state-atom)
                                     :dom-atom (ig/ref ::dom-atom)}}}
        instance (ig/init config)]
    #:ioframe-system {:view (::view instance)
                      :conn (::conn instance)
                      :service-chan (::service-chan instance)}))


(comment

  (def system (create-ioframe-system ioframe-config-sample))

  (defn update! []
    (reagent.dom/render (:ioframe-system/view system)
                        (.getElementById js/document "app")))

  (defn init! []
    (update!)))