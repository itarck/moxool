(ns astronomy.model.user.horizontal-coordinate-tool
  (:require))



(comment

  (def sample
    #:horizontal-coordinate-tool{:target {:db/id [:horizontal-coordinate/name "default"]}

                                 :object/scene [:scene/name "solar"]

                                 :tool/name "horizontal-coordinate-tool-1"
                                 :tool/chinese-name "地平坐标系工具"
                                 :tool/icon "/image/pirate/earth.jpg"

                                 :entity/type :horizontal-coordinate-tool})
  
;;   
  )


;; tx

(defn change-query-args-tx [tool query-args]
  [{:db/id (:db/id tool)
    :tool/query-args query-args}])

(defn change-target-tx [tool target]
  [{:db/id (:db/id tool)
    :tool/target (:db/id target)}])
