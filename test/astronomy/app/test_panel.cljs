(ns test.astronomy.app.test-panel
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <!]]
   [datascript.core :as d]
   [posh.reagent :as p]
   [methodology.lib.chest :as chest]
   [astronomy.app.panel :as app.panel]))



(def system app.panel/system-instance)

(def scene-chan (:system/scene-chan system))

scene-chan

(go (>! scene-chan #:event {:action :scene/change-to-free-mode}))

(go (>! scene-chan #:event {:action :scene/change-to-play-mode}))


(def scene-atom (:system/scene-atom system))
scene-atom

(def conn (:system/conn system))

(d/pull @conn '[*] [:spaceship-camera-control/name "default"])
