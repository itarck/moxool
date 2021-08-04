(ns astronomy.component.line
  (:require
   [applied-science.js-interop :as j]
   ["three" :as three]
   [helix.core :as h :refer [defnc $]]))


(defnc LineComponent [{:keys [points color linewidth]}]
  (let [lineGeometry (three/BufferGeometry.)]
    (j/call lineGeometry :setFromPoints points)
    ($ "line" {:geometry lineGeometry}
       ($ "lineBasicMaterial" {:linewidth (or linewidth 1)
                               :color color
                               :linecap "round"
                               :linejoin "round"}))))