(ns astronomy.model.test-planet
  (:require
   [shu.three.matrix4 :as m4]
   [astronomy.test-conn :refer [create-poshed-conn!]]
   [astronomy.model.planet :as m.planet]
   [astronomy.model.celestial :as m.celestial]))


(def test-conn (create-poshed-conn!))

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


(def planet-fully
  (m.planet/pull-planet-fully @test-conn
                              [:planet/name "earth"]))


(m.planet/cal-local-position planet-1 0.25)
;; => #object[Vector3 [-1.924600834835104 -0.962300417417552 -499.99536986809505]]


(m.planet/cal-local-quaternion planet-1 0.5)
;; => #object[Quaternion [0 -3.9163938347251765e-15 0 1]]


(m.planet/cal-local-matrix planet-1 0.25)
;; => #object[Matrix4 
;;           2.220446049250313e-16, 0, 1, -1.924600834835104
;;           0, 1, 0, -0.962300417417552
;;           -1, 0, 2.220446049250313e-16, -499.99536986809505
;;           0, 0, 0, 1]



(m.planet/cal-world-position planet-fully)
