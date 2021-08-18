(ns astronomy.tools.planet-tool.m
  (:require
   [posh.reagent :as p]))


(def sample-1
  #:planet-tool {:tool/name "planet-tool"
                 :tool/chinese-name "行星"
                 :tool/icon "/image/moxool/goto.jpg"
                 :tool/target {:planet/name "earth"}
                 :entity/type :planet-tool})

(def schema {})



(defn set-target-tx [tool new-target-id]
  [{:db/id (:db/id tool)
    :tool/target new-target-id}])