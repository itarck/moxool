(ns astronomy2.plugin.test-star
  (:require
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


;; helper

(defonce instance
  (sys/init {}))

(def model 
  (::sys/model instance))

;; model 

(model :star/create {:gltf/url "models/16-solar/Sun_1_1391000.glb"
                     :object/scene [:scene/name ::scene]})
;; => {:entity/type :star, :gltf/url "models/16-solar/Sun_1_1391000.glb", :object/scene [:scene/name :astronomy2.plugin.test-star/scene]}



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
