(ns test.astronomy.app.test-panel
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <!]]
   [datascript.core :as d]
   [posh.reagent :as p]
   [methodology.lib.chest :as chest]
   [astronomy.app.panel :as app.panel]))



(def system app.panel/system-instance)

(def meta-chan (:system/meta-chan system))

meta-chan

(go (>! meta-chan #:event {:action :scene/change-to-free-mode}))

(go (>! meta-chan #:event {:action :scene/change-to-play-mode}))


(def meta-atom (:system/meta-atom system))
meta-atom

(def conn (:system/conn system))

(d/pull @conn '[*] [:spaceship-camera-control/name "default"])
