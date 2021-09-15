(ns astronomy.tools.ellipse-orbit-tool.m)



(def ellipse-orbit-tool-1
  {:selector/candinates #{{:db/id [:planet/name "earth"]}
                          {:db/id [:planet/name "mars"]}}
   :selector/selected [:planet/name "earth"]
   :selector/type :static-candinates
   :tool/name "ellipse-orbit-tool"
   :tool/chinese-name "椭圆轨道工具"
   :tool/icon "/image/pirate/earth.jpg"
   :tool/type :ellipse-orbit-tool
   :entity/type :ellipse-orbit-tool})