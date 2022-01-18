(ns astronomy2.plugin.test-star
  (:require
   [astronomy2.plugin.star]
   [astronomy2.app :as app]))


;; data 

(def earth
  #:planet
   {:db/id -1
    :name "earth"
    :chinese-name "地球"
    :radius 5
    :color "blue"
    :star [:star/name "sun"]
    :gltf/url "models/11-tierra/scene.gltf"
    :gltf/scale [0.2 0.2 0.2]

    :object/scene {:db/id [:scene/name "solar"]}
    :object/position [0 0 100]
    :object/quaternion [0 0 0 1]
    :entity/type :planet})



;; view

(comment

  (do
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
       :object/scene [:scene/name ::scene]

       :entity/chinese-name "太阳"
       :entity/type :star})

    (def tx
      [#:scene {:name ::scene
                :background "black"
                :type :astro-scene
                :scale [3 3 3]}
       #:framework{:name "default"
                   :scene [:scene/name ::scene]}
       sun])

    (app/homies :transact! tx))

;; 
  )
