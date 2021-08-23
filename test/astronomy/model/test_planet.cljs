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

(def earth (d/pull test-db11 '[*] [:planet/name "earth"]))

(deftest test-planet-1
  (let [earth (d/pull test-db11 '[*] [:planet/name "earth"])]
    (is (= (planet/cal-system-position-at-epoch-days test-db11 earth 2)
           [439.7765559273832 190.30873698560643 -105.51499959752967]))
    (is (= (planet/cal-system-position-at-epoch-days test-db11 earth 0)
           [442.9497885783528 191.68192377598768 -88.40464973856325]))
    (is (= (d/q planet/query-all-ids test-db11)
           [23 32 36 40 44 48]))))


;; position-log 

(def conn1 (d/conn-from-db test-db11))

(def all-ids (d/q planet/query-all-ids test-db3))

all-ids
;; => [23 32 36 40 44 48]

(d/q planet/query-all-ids-with-tracker test-db11)

(d/transact! conn1 (planet/update-all-world-position @conn1))

(d/transact! conn1 (planet/update-all-position-logs @conn1))

(:planet/position-log (d/pull @conn1 '[*] 32))


(run-tests)