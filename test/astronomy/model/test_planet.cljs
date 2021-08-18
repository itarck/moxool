(ns astronomy.model.test-planet
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [datascript.core :as d]
   [astronomy.conn.core :refer [create-basic-conn!]]
   [astronomy.data.celestial :as d.celestial]
   [posh.reagent :as p]
   [astronomy.objects.planet.m :as planet]
   [astronomy.scripts.test-conn :refer [test-db3 test-db11]]))


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

(def all-ids (d/q planet/query-all-ids test-db3))

all-ids
;; => [23 32 36 40 44 48]

(planet/cal-world-position test-db11 {:db/id 23})

(planet/update-all-world-position test-db11)

;; 

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

