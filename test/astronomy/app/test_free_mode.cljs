(ns astronomy.app.test-free-mode
  (:require
   [posh.reagent :as p]
   [cljs.core.async :refer [go >! timeout <!]]
   [astronomy.app.core :refer [system]]
   [astronomy.app.free-mode :as app.free-mode]
   [astronomy.app.load-gltf :as app.gltf]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]))



(def conn (:system/conn system))

(def service-chan (:system/chan system))

(def dom-atom (:system/dom-atom system))

dom-atom



(go
  (doseq [i (range 10)]
    (<! (timeout 50))
    (let [event-1 #:event {:action :clock-tool/set-time-in-days
                           :detail {:clock {:db/id [:clock/name "default"]}
                                    :time-in-days i}}]
      (go (>! service-chan event-1)))))


(let [event-1 #:event {:action :clock-tool/set-time-in-days
                       :detail {:clock {:db/id [:clock/name "default"]}
                                :time-in-days 0.2}}]
  (go (>! service-chan event-1)))


@(p/pull conn '[*] [:clock/name "default"])

@(p/pull conn '[*] [:coordinate/name "default"])

@(p/pull conn '[*] [:spaceship-camera-control/name "default"])

;; => {:db/id 2, :coordinate/clock #:db{:id 3}, :coordinate/name "default", :coordinate/position [1.2172257975706384 1.2172257975706384 499.99703671393445], :coordinate/quaternion [0 0.587785252292473 0 0.8090169943749475], :coordinate/track-position #:db{:id 6}, :coordinate/track-rotation #:db{:id 6}}


(let [env {:conn conn
           :dom-atom dom-atom}
      spaceship @(p/pull conn '[*] [:spaceship-camera-control/name "default"])]
  (m.spaceship/landing! spaceship env))


@(p/pull conn '[*] [:spaceship-camera-control/name "default"])
;; => {:tool/icon "/image/pirate/cow.jpg", :tool/chinese-name "相机控制", :spaceship-camera-control/name "default", :spaceship-camera-control/mode :orbit-control, :entity/type :spaceship-camera-control, :spaceship-camera-control/up [0 1 0], :spaceship-camera-control/min-distance 8, :tool/name "spaceship camera tool", :db/id 31, :spaceship-camera-control/position [200 200 200], :spaceship-camera-control/target [0 0 0]}


@(p/pull conn '[*] [:planet/name "earth"])
;; => {:object/scene #:db{:id 2}, :celestial/clock #:db{:id 4}, :planet/chinese-name "地球", :entity/type :planet, :planet/name "earth", :object/quaternion [0 0 0 1], :celestial/gltf #:db{:id 8}, :planet/star #:db{:id 5}, :planet/radius 0.08, :db/id 7, :celestial/spin {:db/id 9, :spin/angular-velocity 6.283185307179586, :spin/axis [0 1 0]}, :planet/color "blue", :celestial/orbit {:db/id 10, :circle-orbit/angular-velocity 0.01721420632103996, :circle-orbit/axis [-1 1 0], :circle-orbit/star [:star/name "sun"], :circle-orbit/start-position [0 0 -500]}}


@(p/pull conn '[*] [:coordinate/name "default"])
;; => {:db/id 3, :coordinate/clock #:db{:id 4}, :coordinate/name "default", :coordinate/position [0 0 0], :coordinate/quaternion [0 0 0 1], :coordinate/track-position #:db{:id 7}, :coordinate/track-rotation #:db{:id 7}}


@(p/pull conn '[*] [:camera/name "default"])
;; => {:db/id 1, :camera/far 315360000000000000000N, :camera/name "default", :camera/near 0.001, :camera/position [13.407380197618506 414.8546200519799 479.9417257887149], :camera/quaternion [-0.3487719629638101 0.013086644847652557 0.004870597946723298 0.937103588112138]}

;; => {:db/id 1, :camera/far 315360000000000000000N, :camera/name "default", :camera/near 0.001, :camera/position [-414.5928298778137 187.75662912590357 442.14070996513124], :camera/quaternion [-0.13914626069289482 -0.3636455584538221 -0.05503335784712415 0.9194408928669984]}

@(p/pull conn '[*] [:satellite/name "moon"])
;; => {:object/scene #:db{:id 2}, :celestial/clock #:db{:id 4}, :satellite/planet #:db{:id 7}, :satellite/radius 0.00579, :satellite/chinese-name "月球", :entity/type :satellite, :object/quaternion [0 0 0.2036417511401775 0.9790454724845838], :celestial/gltf #:db{:id 12}, :object/position [0 0 1], :satellite/name "moon", :db/id 11, :celestial/spin {:db/id 13, :spin/angular-velocity 0.23271056693257727, :spin/axis [-0.3987490689252462 0.917060074385124 0]}, :satellite/color "green", :celestial/orbit {:db/id 14, :circle-orbit/angular-velocity 0.20943951023931953, :circle-orbit/axis [-0.3987490689252462 0.917060074385124 0], :circle-orbit/start-position [0 0 1]}}
