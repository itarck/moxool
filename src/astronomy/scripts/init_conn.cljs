(ns astronomy.scripts.init-conn
  (:require
   [cljs.reader :refer [read-string]]
   [cljs.core.async :refer [go >! <! chan]]
   [cljs-http.client :as http]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [shu.goog.math :as gmath]
   [shu.three.vector3 :as v3]
   [shu.three.quaternion :as q]
   [shu.astronomy.equatorial :as eq]
   [methodology.model.user.backpack :as m.backpack]
   [methodology.model.core :as mtd-model]
   [astronomy.model.core :as ast-model]
   [astronomy.model.clock :as m.clock]
   [astronomy.model.star :as m.star]
   [astronomy.model.user.clock-tool :as m.clock-tool]))



(defn period-to-angular-velocity [period]
  (/ (* 2 Math/PI) period))

(def schema (merge ast-model/schema
                   mtd-model/schema))


(def ecliptic-quaternion
  (let [ang 23.5]
    (vec (q/from-unit-vectors
          (v3/vector3 0 1 0)
          (v3/normalize (v3/from-seq [(- (Math/sin (gmath/to-radians ang)))
                                      (Math/cos (gmath/to-radians ang))
                                      0]))))))

(def ecliptic-axis
  (let [ang 23.5]
    [(- (Math/sin (gmath/to-radians ang)))
     (Math/cos (gmath/to-radians ang))
     0]))

(def lunar-axis
  (let [ang (+ 23.4 5.15)]
    [(- (Math/sin (gmath/to-radians ang)))
     (Math/cos (gmath/to-radians ang))
     0]))

ecliptic-axis
lunar-axis

(def equatorial-quaternion [0 0 0 1])


ecliptic-axis

(def camera
  #:camera{:name "default"
           :far (* 1e10 365 86400 1000)
           :near 0.001
           :position [200 200 200]
           :quaternion [0 0 0 1]})


(def clock
  #:clock {:name "default"
           :time-in-days 0.5})

(def scene
  #:astro-scene {:coordinate -10
                 :camera [:camera/name "default"]
                 :clock [:clock/name "default"]
                 :scene/name "solar"
                 :scene/chinese-name "太阳系"
                 :scene/scale 100
                 :entity/type :scene})

(def coordinate-1
  #:coordinate {:db/id -10
                :name "default"
                :clock [:clock/name "default"]
                :track-position [:planet/name "earth"]
                :track-rotation [:planet/name "earth"]
                :position [0 0 0]
                :quaternion [0 0 0 1]})


(def sun
  #:star{:name "sun"
         :chinese-name "太阳"
         :radius 2.32
         :color "red"
         :celestial/chinese-name "太阳"
         :celestial/gltf #:gltf {:url "models/12-sun_and_solar_flares/scene.gltf"
                                 :scale [1.12 1.12 1.12]}
         :object/position [0 0 0]
         :object/quaternion ecliptic-quaternion
         :object/scene [:scene/name "solar"]
         :entity/type :star})


(def earth
  #:planet
   {:name "earth"
    :chinese-name "地球"
    :radius 0.021
    :color "blue"
    :star [:star/name "sun"]
    :celestial/chinese-name "地球"
    :celestial/orbit #:circle-orbit {:star [:star/name "sun"]
                                     :start-position [0 0 -500]
                                     :axis ecliptic-axis
                                     :angular-velocity (period-to-angular-velocity 365.25)}
    :celestial/spin #:spin {:axis [0 1 0]
                            :angular-velocity (period-to-angular-velocity 0.99726968)}
    :celestial/gltf #:gltf {:url "models/15-earth/scene.gltf"
                            :scale [0.01 0.01 0.01]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    :object/quaternion [0 0 0 1]
    :entity/type :planet})

(def moon
  #:satellite
   {:name "moon"
    :chinese-name "月球"
    :radius 0.00579
    :color "green"
    :planet [:planet/name "earth"]
    :celestial/chinese-name "月球"
    :celestial/orbit #:circle-orbit {:start-position [0 0 1.281]
                                     :axis lunar-axis
                                     :angular-velocity (period-to-angular-velocity 27)}
    :celestial/spin #:spin {:axis ecliptic-axis
                            :angular-velocity (period-to-angular-velocity 27)}
    :celestial/gltf #:gltf {:url "models/14-moon/scene.gltf"
                            :scale [0.01 0.01 0.01]}
    :celestial/clock [:clock/name "default"]
    :object/scene [:scene/name "solar"]
    ;; :object/position [0 0 30]
    :object/quaternion [0 0 0 1]
    :entity/type :satellite})


;; * 银心：在天球赤道座标系统的座标是：
;; 赤经 17h45m40.04s，赤纬 -29º 00' 28.1"（J2000 分点）。25000光年

;; * 北银极：换算成2000.0历元的坐标，北银极位于赤经12h 51m 26.282s，赤纬+27° 07′ 42.01″（2000.0历元），银经0度的位置角是122.932°.[4]

(def galaxy-center [(eq/to-distance 25000)
                    (eq/to-declination -29 00 28.1)
                    (eq/to-right-ascension 17 45 40.04)])

(def galaxy-north [1
                   (eq/to-declination 27 07 42.01)
                   (eq/to-right-ascension 12 51 26.282)])

(def galaxy-center-vector (apply eq/cal-position galaxy-center))

(def galxy-north-vector
  (v3/normalize (apply eq/cal-position galaxy-north)))

(def galaxy-quaternion (q/from-unit-vectors (v3/vector3 0 1 0) galxy-north-vector))


galaxy-center-vector
;; => #object[Vector3 [-668315803846.49 -382317840299.07935 -169592497823.65826]]

galaxy-quaternion

(def galaxy
  #:galaxy
   {:name "milky way"
    :chinese-name "银河"
    :radius (* 150000 365 86400)
    :celestial/gltf #:gltf{:url "models/13-galaxy/scene.gltf"
                           :scale (-> (v3/from-seq [0.01 0.005 0.01])
                                      (v3/multiply-scalar 0.3)
                                      seq)
                           :position (-> (v3/from-seq [-1.12 -0.57 1.12])
                                         (v3/multiply-scalar 0.3)
                                         seq)}
    :object/position (vec galaxy-center-vector)
    :object/quaternion (vec galaxy-quaternion)
    :object/scene [:scene/name "solar"]
    :entity/type :galaxy})


(def person1
  #:person {:db/id -1
            :name "dr who"
            :backpack #:backpack {:db/id -3
                                  :name "default"
                                  :owner -1
                                  :cell (vec (for [i (range 10)]
                                               #:backpack-cell{:index i}))}
            :entity/type :person})


(def clock-tool1
  #:clock-tool {:db/id -2
                :status :stop
                :steps-per-second 20
                :step-interval :hour
                :days-per-step (/ 1 24)
                :clock [:clock/name "default"]
                :tool/name "clock control 1"
                :tool/chinese-name "时钟1"
                :tool/icon "/image/pirate/sheep.jpg"
                :entity/type :clock-tool})


(def info-tool
  #:info-tool {:tool/name "info tool 1"
               :tool/chinese-name "信息查询工具"
               :tool/icon "/image/pirate/cow.jpg"
               :entity/type :info-tool})

(def spaceship-camera-control
  #:spaceship-camera-control
   {:name "default"
    :mode :orbit-control
    :min-distance 2.1
    :position [200 200 200]
    :up [0 1 0]
    :target [0 0 0]
    :tool/name "spaceship camera tool"
    :tool/chinese-name "相机控制"
    :tool/icon "/image/pirate/cow.jpg"
    :entity/type :spaceship-camera-control})

(def coordinate-tool-1
  #:coordinate-tool {:coordinate [:coordinate/name "default"]
                     :tool/name "coordinate tool 1"
                     :tool/chinese-name "坐标系设置工具"
                     :tool/icon "/image/pirate/earth.jpg"
                     :entity/type :coordinate-tool})

;; processes


(defn kick-start! [conn]
  (let [clock-id [:clock/name "default"]
        time-in-days 0
        tx (m.clock-tool/update-by-clock-time-tx @conn clock-id time-in-days)]
    (p/transact! conn tx)))


(defn init-conn! []
  (let [conn (d/create-conn schema)]
    (d/transact! conn [camera clock scene sun earth
                       moon coordinate-1
                       galaxy])
    (d/transact! conn [person1 clock-tool1
                       info-tool spaceship-camera-control
                       coordinate-tool-1])

    (let [person (d/pull @conn '[*] [:person/name "dr who"])
          bp (d/pull @conn '[*] (-> person :person/backpack :db/id))]
      (d/transact! conn (m.backpack/put-in-cell-tx bp 0 {:db/id [:tool/name "clock control 1"]}))
      (d/transact! conn (m.backpack/put-in-cell-tx bp 1 {:db/id [:tool/name "info tool 1"]}))
      (d/transact! conn (m.backpack/put-in-cell-tx bp 2 {:db/id [:tool/name "coordinate tool 1"]})))

    (kick-start! conn)
    conn))

(defn create-db []
  (let [conn (init-conn!)]
    @conn))


(defn async-prepare! []
  (let [ch (chan)
        local-ref (atom {})]
    (go
      (let [response (<! (http/get "/edn/stars.edn"))]
        (swap! local-ref assoc :stars (read-string (:body response))))
      (let [response (<! (http/get "/edn/constellation1.edn"))
            constel1 (read-string (:body response))]
        (swap! local-ref assoc :constellations1 constel1))
      (let [response (<! (http/get "/edn/constellation2.edn"))]
        (swap! local-ref assoc :constellations2 (read-string (:body response))))
      (>! ch @local-ref))
    ch))


(defn load-stars! [conn stars]
  (let [tx (mapv m.star/parse-raw-bsc-data stars)]
    (d/transact! conn tx)))

(defn parse-star-line [conn HR-line]
  (mapv (fn [HR] (:db/id (d/pull @conn '[:db/id] [:star/HR HR]))) HR-line))

(defn parse-constellation [conn constellation]
  (let [star-lines (vec
                    (for [line (:constellation/star-HR-lines constellation)]
                      (parse-star-line conn line)))
        abbreviation (:constellation/abbreviation constellation)]
    #:constellation {:abbreviation abbreviation
                     :star-lines star-lines}))

(defn load-constellations1! [conn constellations]
  (let [tx (mapv (fn [constel] (parse-constellation conn constel)) constellations)] 
    (d/transact! conn tx)))

(defn load-constellations2! [conn constellations]
  (d/transact! conn constellations))

(defn load-dataset! [conn dataset]
  (let [{:keys [stars constellations1 constellations2]} dataset]
    (load-stars! conn stars)
    (load-constellations1! conn constellations1)
    (load-constellations2! conn constellations2)))


(defn async-run! []
  (let [ch (chan)
        conn (init-conn!)]
    (go
      (let [dataset (<! (async-prepare!))]
        (load-dataset! conn dataset))
      (let [db-name "free-mode.edn"
            response (<! (http/post "/api/db/save" {:edn-params {:db-name db-name
                                                                 :db-value (dt/write-transit-str @conn)}}))]
        (println (:body response)))
      (>! ch @conn))
    ch))


(async-run!)


(comment

  (def conn (init-conn!))

  (def dataset {})

  (go (let [v (<! (async-prepare!))]
        (set! dataset v)))

  (keys dataset)
  ;; => (:stars :constellations1 :constellations2)


  (def stars (:stars dataset))

  (def constellations (:constellations2 dataset))

  
  (first stars)
  (count constellations)
  
  dataset

  (m.star/parse-raw-bsc-data (first stars))

  ;; => #:star{:DEm 13, :HR 1, :RAh 0, :RAm 5, :DEd 45, :HD 3, :right-ascension 0.08608333333333333, :visual-magnitude 6.7, :DEs 45, :RAs 9.9, :declination 45.22916666666667}


  (load-dataset! conn dataset)

  (d/pull @conn '[*] [:star/HR 3890])

  (d/pull @conn '[*] [:constellation/abbreviation "And"])

  (async-run!)

  ;; 
  )