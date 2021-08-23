(ns astronomy.model.test-planet
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [datascript.core :as d]
   [astronomy.objects.planet.m :as planet]
   [astronomy.model.coordinate :as coordinate]
   [astronomy.scripts.test-conn :refer [test-db11]]))


(def planet-1
  #:planet {:name "earth"
            :chinese-name "地球"
            :radius 2
            :color "blue"
            :star [:star/name "sun"]

            :celestial/orbit #:circle-orbit {:star [:star/name "sun"]
                                             :start-position [0 0 -500]
                                             :axis [-1 2 0]
                                             :period 365}
            :celestial/spin #:spin {:axis [0 1 0]
                                    :period 1}
            :celestial/gltf #:gltf {:model-url "models/11-tierra/scene.gltf"
                                    :model-scale [0.2 0.2 0.2]}

            :object/scene [:scene/name "solar"]
            :object/position [100 0 0]
            :entity/type :planet})


;; test model

(def earth (d/pull test-db11 '[*] [:planet/name "earth"]))
(def mercury (d/pull test-db11 '[*] [:planet/name "mercury"]))

(def ac-1 (d/pull test-db11 '[*] [:coordinate/name "赤道天球坐标系"]))

(deftest test-planet-1
  (let [earth (d/pull test-db11 '[*] [:planet/name "earth"])]
    (is (= (planet/cal-system-position-at-epoch test-db11 earth 2)
           [439.7765559273832 190.30873698560643 -105.51499959752967]))
    (is (= (planet/cal-system-position-at-epoch test-db11 earth 0)
           [442.9497885783528 191.68192377598768 -88.40464973856325]))
    (is (= (d/q planet/query-all-ids test-db11)
           [23 32 36 40 44 48]))
    
    (is (= (planet/cal-coordinate-position-at-epoch test-db11 mercury ac-1 0)
           [-309.44533427268226 -123.98534769993685 122.15004660287056]))
    ))


(run-tests)
