(ns astronomy.service.test-clock-control
  (:require 
   [posh.reagent :as p]
   [astronomy.test-conn :refer [create-poshed-conn!]]
   [astronomy.service.clock-control :as service]))


(def conn (create-poshed-conn!))



@(p/pull conn '[*] 5)
;; => {:object/scene #:db{:id 1}, :celestial/clock #:db{:id 3}, :planet/chinese-name "地球", :entity/type :planet, :planet/name "earth", :object/quaternion [0 0 0 1], :celestial/gltf #:db{:id 6}, :object/world-position [0 0 100], :planet/star #:db{:id 4}, :object/position [0 0 100], :planet/radius 5, :db/id 5, :celestial/spin {:db/id 7, :spin/axis [0 1 0], :spin/period 1}, :planet/color "blue", :celestial/orbit {:db/id 8, :circle-orbit/axis [-1 1 0], :circle-orbit/period 365, :circle-orbit/star [:star/name "sun"], :circle-orbit/start-position [0 0 100]}}
