(ns astronomy.app.test-film2
  (:require
   [film2.app.core :as app]
   [cljs.core.async :refer [go >!]]
   [posh.reagent :as p]
   [datascript.core :as d]))


(def system app/system)


(keys system)
;; => (:studio/conn :studio/instance-atom :studio/service-chan :studio/service-center :studio/view)


(def instance @(:studio/instance-atom system))

(keys (get-in instance [:iovideo]))

(def ioframe-system (get-in instance [:iovideo 5]))

(keys ioframe-system)
;; => (:ioframe-system/view :ioframe-system/conn :ioframe-system/dom-atom :ioframe-system/meta-atom)


@(get-in ioframe-system [:ioframe-system/dom-atom])
;; => {:camera #object[PerspectiveCamera [object Object]], :three-instance #js {:active true, :colorManagement true, :concurrent false, :aspect 1.7777777777777777, :camera #object[PerspectiveCamera [object Object]], :scene #object[Scene [object Object]], :raycaster #object[Raycaster [object Object]], :mouse #object[Vector2 [object Object]], :clock #object[Clock [object Object]], :gl #object[WebGLRenderer [object Object]], :size #js {:left 0, :top 80, :width 1280, :height 720, :bottom 800, :right 1280, :x 0, :y 80}, :viewport #object[Function], :pointer #js {:e #js {}, :on #object[on], :once #object[once], :emit #object[emit], :off #object[off]}, :events #js {:onClick #object[Function], :onContextMenu #object[Function], :onDoubleClick #object[Function], :onWheel #object[Function], :onPointerDown #object[Function], :onPointerUp #object[Function], :onPointerLeave #object[onPointerLeave], :onPointerMove #object[Function], :onGotPointerCaptureLegacy #object[onGotPointerCaptureLegacy], :onLostPointerCapture #object[onLostPointerCapture]}, :subscribe #object[subscribe], :setDefaultCamera #object[setDefaultCamera], :invalidate #object[invalidate], :intersect #object[intersect], :forceResize #object[callback]}, :scene #object[Scene [object Object]], :spaceship-camera-control #object[CameraControls [object Object]]}
