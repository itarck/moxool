(ns astronomy.model.test-planet
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [astronomy.conn.core :refer [create-basic-conn!]]
   [astronomy.data.celestial :as d.celestial]
   [posh.reagent :as p]))


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

(def conn
  (let [conn (create-basic-conn!)]
    (p/transact! conn d.celestial/dataset1)
    conn))


(def earth
  @(p/pull conn '[* {:planet/star [*]}]
           [:planet/name "earth"]))

(:celestial/orbit earth)

(deftest test-planet
  (is (= (get-in earth [:planet/star :star/name])
         "sun")))

(run-tests)

