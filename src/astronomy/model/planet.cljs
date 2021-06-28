(ns astronomy.model.planet
  (:require
   [datascript.core :as d]
   [shu.goog.math :as gmath]
   [shu.three.vector3 :as v3]
   [shu.three.spherical :as sph]
   [shu.three.quaternion :as q]
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

(defn is-coordinate-center? [planet coordinate]
  (= (:db/id planet) (get-in coordinate [:coordinate/track-position :db/id])))

(defn random-position [radius axis]
  (let [v1 (v3/from-spherical (sph/spherical radius (/ Math/PI 2) (* Math/PI 2 (rand))))
        q1 (q/from-unit-vectors (v3/vector3 0 1 0) (v3/from-seq axis))]
    (seq (v3/apply-quaternion v1 q1))))


(comment
  
  (def ecliptic-axis
    (let [ang 23.4]
      [(- (Math/sin (gmath/to-radians ang)))
       (Math/cos (gmath/to-radians ang))
       0]))
  
  (random-position 100 ecliptic-axis))