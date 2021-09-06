(ns astronomy.tools.satellite-tool.m
  (:require
   [posh.reagent :as p]))


(def sample-1
  #:satellite-tool {:tool/name "satellite-tool"
                    :tool/chinese-name "卫星工具"
                    :tool/icon "/image/moxool/goto.jpg"
                    :tool/target {:satellite/name "earth"}
                    :entity/type :satellite-tool})

(def schema {})



(defn set-target-tx [tool new-target-id]
  [{:db/id (:db/id tool)
    :tool/target new-target-id}])