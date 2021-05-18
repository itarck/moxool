(ns astronomy.model.planet
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]))

;; 包含ns: planet


;; model

(def schema {:planet/name {:db/unique :db.unique/identity}
             :planet/star {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

;; data 

(def planet-1
  #:planet
   {:name "earth"
    :chinese-name "地球"
    :radius 5
    :color "blue"
    :star [:star/name "sun"]

    :celestial/orbit #:circle-orbit {:star [:star/name "sun"]
                                     :start-position [0 0 100]
                                     :axis [-1 1 0]
                                     :angular-velocity (/ Math/PI 180)}
    :celestial/spin #:spin {:axis [0 1 0]
                            :angular-velocity (* Math/PI 2)}
    :celestial/gltf #:gltf {:url "models/11-tierra/scene.gltf"
                            :scale [0.2 0.2 0.2]}
    :celestial/clock [:clock/name "default"]
    
    :object/scene [:scene/name "solar"]
    :object/position [0 0 100]
    :object/quaternion [0 0 0 1]
    :entity/type :planet})



;; model 


(defn cal-world-position [db planet-id]
  (let [planet (d/pull db '[*] planet-id)
        star (d/pull db '[*] (-> planet :planet/star :db/id))]
    (mapv + (:object/position planet)
          (:object/position star))))
