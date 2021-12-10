(ns astronomy.scripts.angel.repl
  (:require
   [posh.reagent :as p]
   [astronomy.app.solar :as app.solar]
   [astronomy.lib.api :as api]
   [cljs.core.async :refer [go >!]]
   [astronomy.scripts.angel.lib :as slib]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.three.euler :as e]
   ))


;; value and refs 

(def conn
  (:astronomy/conn app.solar/system))

(def service-chan
  (:astronomy/service-chan app.solar/system))

(def planets-5
  [{:db/id [:planet/name "mercury"]}
   {:db/id [:planet/name "venus"]}
   {:db/id [:planet/name "earth"]}
   {:db/id [:planet/name "mars"]}
   {:db/id [:planet/name "jupiter"]}
   {:db/id [:planet/name "saturn"]}])

(def satellites
  [{:db/id [:satellite/name "moon"]}
   {:db/id [:satellite/name "io"]}
   {:db/id [:satellite/name "europa"]}
   {:db/id [:satellite/name "ganymede"]}
   {:db/id [:satellite/name "callisto"]}])

;; help functions


(defn dispatch
  [event]
  (go (>! service-chan event)))


(defn re-frash-camera! []
  (go (>! service-chan 
          #:event{:action :spaceship-camera-control/refresh-camera
                  :detail {:spaceship-camera-control {:db/id [:spaceship-camera-control/name "default"]}}})))


(defn change-sun-light [show?]
  (p/transact! conn [{:db/id [:star/name "sun"]
                      :star/show-light? show?}]))

(defn change-scene-ambient-light [intensity]
  (p/transact! conn [{:scene/name "solar"
                      :scene/ambient-light-intensity intensity}]))

(defn set-moon-rotation! []
  (let [moon @(p/pull conn '[*] [:satellite/name "moon"])
        q1 (q/from-axis-angle (v3/from-seq [0 1 0]) (/ Math/PI 2))
        q2 (q/from-unit-vectors (v3/from-seq [0 0 -1])
                                (v3/normalize (v3/from-seq [-0.92099222494957 -0.2626882147761619 -1.034279284990182])))
        rotation (e/from-quaternion (q/multiply q1 q2))]
    (p/transact! conn
                 [{:db/id (:db/id (:celestial/gltf moon))
                   :gltf/rotation rotation}])))

;; processes


(comment ;; scene 1-1 

  (slib/init-tool! conn slib/all-tools)
  
  (let [tools-1-1 [{:db/id [:tool/name "clock control 1"]}
                   {:db/id [:tool/name "planet-tool"]}]]
    (slib/init-tool! conn tools-1-1))

  (change-sun-light false)
  (re-frash-camera!)
  
  (let [db-url "/frame/dev/scene-1-1-v2.fra"]
    (api/save-db-file @conn db-url))
;;
  )


(comment  ;; scene 1-2

  (slib/init-tool! conn slib/all-tools)

  (let [tools-1-2 [{:db/id [:tool/name "clock control 1"]}
                   {:db/id [:tool/name "planet-tool"]}
                   {:db/id [:tool/name "constellation-tool"]}
                   {:db/id [:tool/name "astronomical-coordinate-tool"]}]]
    (slib/init-tool! conn tools-1-2))

  (change-sun-light false)
  (re-frash-camera!)

  (let [db-url "/frame/dev/scene-1-2-v2.fra"]
    (api/save-db-file @conn db-url))

;;
  )


(comment  ;; scene 1-3

  (slib/init-tool! conn slib/all-tools)

  (let [tools-1-3 [{:db/id [:tool/name "clock control 1"]}
                   {:db/id [:tool/name "planet-tool"]}
                   {:db/id [:tool/name "constellation-tool"]}
                   {:db/id [:tool/name "astronomical-coordinate-tool"]}
                   {:db/id [:tool/name "terrestrial-coordinate-tool"]}
                   {:db/id [:tool/name "horizon-coordinate-tool"]}]]
    (slib/init-tool! conn tools-1-3))

  (change-sun-light false)
  (re-frash-camera!)

  (let [db-url "/frame/dev/scene-1-3-v2.fra"]
    (api/save-db-file @conn db-url))
;;
  )


(comment  ;; scene 2-1

  (slib/init-tool! conn slib/all-tools)

  (let [tools-2-1 [{:db/id [:tool/name "clock control 1"]}
                   {:db/id [:tool/name "planet-tool"]}
                   {:db/id [:tool/name "constellation-tool"]}
                   {:db/id [:tool/name "astronomical-coordinate-tool"]}
                   {:db/id [:tool/name "terrestrial-coordinate-tool"]}
                   {:db/id [:tool/name "horizon-coordinate-tool"]}]]
    (slib/init-tool! conn tools-2-1))

  (change-sun-light true)
  (change-scene-ambient-light 0.1)
  (re-frash-camera!)

  (let [db-url "/frame/dev/scene-2-1-v1.fra"]
    (api/save-db-file @conn db-url))
;;
  )


(comment  ;; scene 2-2

  (slib/init-tool! conn slib/all-tools)

  (let [tools-2-2 [{:db/id [:tool/name "clock control 1"]}
                   {:db/id [:tool/name "planet-tool"]}
                   {:db/id [:tool/name "constellation-tool"]}
                   {:db/id [:tool/name "astronomical-coordinate-tool"]}
                   {:db/id [:tool/name "terrestrial-coordinate-tool"]}
                   {:db/id [:tool/name "horizon-coordinate-tool"]}]]
    (slib/init-tool! conn tools-2-2))

  (change-sun-light true)
  (change-scene-ambient-light 0.1)
  (re-frash-camera!)

  (let [db-url "/frame/dev/scene-2-2-v1.fra"]
    (api/save-db-file @conn db-url))
;;
  )



(comment  ;; scene 2-3

  (slib/init-tool! conn slib/all-tools)
  
  (let [tools-2-3 [{:db/id [:tool/name "clock control 1"]}
                   {:db/id [:tool/name "planet-tool"]}
                   {:db/id [:tool/name "constellation-tool"]}
                   {:db/id [:tool/name "astronomical-coordinate-tool"]}
                   {:db/id [:tool/name "terrestrial-coordinate-tool"]}
                   {:db/id [:tool/name "horizon-coordinate-tool"]}]]
    (slib/init-tool! conn tools-2-3))

  (change-sun-light true)
  (change-scene-ambient-light 0.1)
  (re-frash-camera!)

  (let [db-url "/frame/dev/scene-2-3-v1.fra"]
    (api/save-db-file @conn db-url))
;;
  )



(comment  ;; scene 3-1

  (slib/init-tool! conn slib/all-tools)

  (let [tools-3-1 [{:db/id [:tool/name "clock control 1"]}
                   {:db/id [:tool/name "planet-tool"]}
                   {:db/id [:tool/name "constellation-tool"]}
                   {:db/id [:tool/name "astronomical-coordinate-tool"]}
                   {:db/id [:tool/name "terrestrial-coordinate-tool"]}
                   {:db/id [:tool/name "horizon-coordinate-tool"]}]]
    (slib/init-tool! conn tools-3-1))

  (slib/init-scene! conn planets-5)

  (change-sun-light true)
  (change-scene-ambient-light 0.1)
  (re-frash-camera!)

  (let [db-url "/frame/dev/scene-3-1-v1.fra"]
    (api/save-db-file @conn db-url))
;;
  )


(comment  ;; scene 3-2

  (slib/init-tool! conn slib/all-tools)

  (let [tools-3-1 [{:db/id [:tool/name "clock control 1"]}
                   {:db/id [:tool/name "planet-tool"]}
                   {:db/id [:tool/name "constellation-tool"]}
                   {:db/id [:tool/name "astronomical-coordinate-tool"]}
                   {:db/id [:tool/name "terrestrial-coordinate-tool"]}
                   {:db/id [:tool/name "horizon-coordinate-tool"]}]]
    (slib/init-tool! conn tools-3-1))

  (slib/init-scene! conn planets-5)

  (change-sun-light true)
  (change-scene-ambient-light 0.1)
  (re-frash-camera!)

  (let [db-url "/frame/dev/scene-3-2-v1.fra"]
    (api/save-db-file @conn db-url))
;;
  )


(comment  ;; scene 3-3

  (slib/init-tool! conn slib/all-tools)

  (let [tools-3-3 [{:db/id [:tool/name "clock control 1"]}
                   {:db/id [:tool/name "planet-tool"]}
                   {:db/id [:tool/name "constellation-tool"]}
                   {:db/id [:tool/name "astronomical-coordinate-tool"]}
                   {:db/id [:tool/name "terrestrial-coordinate-tool"]}
                   {:db/id [:tool/name "horizon-coordinate-tool"]}]]
    (slib/init-tool! conn tools-3-3))

  (slib/init-scene! conn planets-5)

  (change-sun-light true)
  (change-scene-ambient-light 0.1)
  (re-frash-camera!)

  (let [db-url "/frame/dev/scene-3-3-v2.fra"]
    (api/save-db-file @conn db-url))
;;
  )


(comment  ;; scene 4-1

  (slib/init-tool! conn slib/all-tools)

  (let [tools-4-1 [{:db/id [:tool/name "clock control 1"]}
                   {:db/id [:tool/name "planet-tool"]}
                   {:db/id [:tool/name "satellite-tool"]}
                   {:db/id [:tool/name "constellation-tool"]}
                   {:db/id [:tool/name "astronomical-coordinate-tool"]}
                   {:db/id [:tool/name "terrestrial-coordinate-tool"]}
                   {:db/id [:tool/name "horizon-coordinate-tool"]}
                   {:db/id [:tool/name "spaceship camera tool"]}]]
    (slib/init-tool! conn tools-4-1))

  (slib/init-scene! conn (concat planets-5 satellites))

  (change-sun-light true)
  (change-scene-ambient-light 0.1)
  (re-frash-camera!)

  (let [db-url "/frame/dev/scene-4-1-v1.fra"]
    (api/save-db-file @conn db-url))


  ;;
  )


(comment  ;; scene 4-2

  (slib/init-tool! conn slib/all-tools)

  (let [tools-4-2 [{:db/id [:tool/name "clock control 1"]}
                   {:db/id [:tool/name "planet-tool"]}
                   {:db/id [:tool/name "satellite-tool"]}
                   {:db/id [:tool/name "constellation-tool"]}
                   {:db/id [:tool/name "astronomical-coordinate-tool"]}
                   {:db/id [:tool/name "terrestrial-coordinate-tool"]}
                   {:db/id [:tool/name "horizon-coordinate-tool"]}
                   {:db/id [:tool/name "spaceship camera tool"]}]]
    (slib/init-tool! conn tools-4-2))

  (slib/init-scene! conn (concat planets-5 satellites))

  (change-sun-light true)
  (change-scene-ambient-light 0.1)
  (re-frash-camera!)

  (let [db-url "/frame/dev/scene-4-2-v1.fra"]
    (api/save-db-file @conn db-url))


  (def moon-orbit
    (:celestial/orbit @(p/pull conn '[*] [:satellite/name "moon"])))

  (:db/id moon-orbit)

  ;;
  )


(comment ;; scene full

  #_(slib/init-tool! conn slib/all-tools)

  (re-frash-camera!)

  (slib/init-scene! conn satellites)

  (set-moon-rotation!)

  (let [db-url "/frame/dev/full-v1.fra"]
    (api/save-db-file @conn db-url))
;;
  )