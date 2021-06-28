(ns astronomy.app.test-free-mode
  (:require
   [applied-science.js-interop :as j]
   [datascript.core :as d]
   [posh.reagent :as p]
   [cljs.core.async :refer [go >! timeout <!]]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.model.constellation :as m.constel]
   [astronomy.app.scene-free :as scene-free]
   [astronomy.app.core :refer [free-app-instance]]
   [methodology.model.scene :as m.scene]
   [shu.three.vector3 :as v3]
   ["three" :as three]
   [astronomy.model.atmosphere :as m.atm]))

;; 

(def test-app-instance (scene-free/create-app! {}))


;; test instance 

(keys free-app-instance)

(def conn (:app/scene-conn free-app-instance))

(count (m.scene/sub-objects conn [:scene/name "solar"]))

(def service-chan (:app/service-chan free-app-instance))

(def dom-atom (get-in free-app-instance [:app/scene-system :system/dom-atom]))

(keys @dom-atom)

(def mouse (j/get-in (:three-instance @dom-atom) [:mouse]))

(seq (j/call mouse :toArray))

(j/get-in (:three-instance @dom-atom) [:size :width])

(def earth-object (j/get (get-in @dom-atom [8]) :current))

earth-object

(:celestial/gltf @(p/pull conn '[*] [:planet/name "earth"]))

(j/get (:scene @dom-atom) :children)


(go (>! service-chan #:event {:action :universe-tool/change-celestial-scale
                              :detail {:scene-id [:scene/name "solar"]
                                       :celestial-scale 10}}))

(def atmo1 (m.atm/sub-unique-one conn))

atmo1

@(p/pull conn '[*] [:coordinate/name "default"])
;; => {:db/id 4, :coordinate/clock #:db{:id 2}, :coordinate/name "default", :coordinate/position [0 0 -498.6596333], :coordinate/quaternion [0 0 0 1], :coordinate/track-position #:db{:id 8}, :coordinate/track-rotation #:db{:id 8}}


(m.atm/sun-position-vector atmo1)


@(p/pull conn '[*] [:constellation/abbreviation "Mic"])
;; => {:constellation/chinese-name "显微镜座", :constellation/star-lines [[8192 8176 8080 8006]], :constellation/abbreviation "Mic", :constellation/right-ascension 300.9646666666667, :constellation/latin-name "Microscopium", :constellation/declination -35.72516666666667, :db/id 9204, :constellation/group "拉卡伊", :constellation/quadrant "SQ4", :constellation/area 209.513}


