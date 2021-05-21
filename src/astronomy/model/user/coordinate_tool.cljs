(ns astronomy.model.user.coordinate-tool
  (:require
   [posh.reagent :as p])
  )



(def coordinate-tool-1
  {:tool/name "coordinate tool"
   :tool/chinese-name "坐标系设置工具"
   :tool/icon "/image/pirate/earth.jpg"
   :tool/backpack {:backpack/name "default"}
   :entity/type :coordinate-tool})



(defn sub-coordinate-tool [conn id]
  @(p/pull conn '[*] id))