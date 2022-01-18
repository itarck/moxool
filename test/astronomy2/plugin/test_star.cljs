(ns astronomy2.plugin.test-star
  (:require
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [datascript.core :as d]
   [astronomy2.system :as sys]
   [astronomy2.plugin.star]
   [astronomy2.app :as app]
   [astronomy2.db :as db]))

;; data 

(def sun
  {:star/name "sun"
   :star/chinese-name "太阳"
   :star/show-light? true

   :celestial/radius 2.321606103
   :celestial/radius-string "109.1 地球半径"

   :gltf/url "models/16-solar/Sun_1_1391000.glb"
   :gltf/scale [0.002 0.002 0.002]
   :gltf/shadow? false

   :object/type :star
   :object/position [0 0 0]
   :object/quaternion [0 0 0 1]
   :object/show? true
   :object/scene [:scene/name "default"]})


(def test-db
  (let [basic-db (db/create-db :basic)]
    (d/db-with basic-db [sun])))

(def sun-1
  (d/pull test-db '[*] [:star/name "sun"]))

;; helper

(defonce instance
  (sys/init {}))

(def spec 
  (::sys/spec instance))

(def model 
  (::sys/model instance))

;; spec 

(spec :valid? :star/star sun-1)

;; model 

(deftest test-model
  (testing "testing model"
    (is (= (model :star/create {:gltf/url "models/16-solar/Sun_1_1391000.glb"
                                :object/scene [:scene/name ::scene]})
           {:object/type :star, :gltf/url "models/16-solar/Sun_1_1391000.glb"
            :object/scene [:scene/name :astronomy2.plugin.test-star/scene]}))))

;; view


(comment

  (do
    
    (def tx
      [(model :scene/create {:scene/name ::scene
                             :scene/background "black"})
       #:framework{:name "default"
                   :scene [:scene/name ::scene]}
       (model :star/create {:gltf/url "models/16-solar/Sun_1_1391000.glb"
                            :gltf/scale [0.002 0.002 0.002]
                            :object/scene [:scene/name ::scene]})])

    (app/homies :transact! tx))

;; 
  )


(run-tests)