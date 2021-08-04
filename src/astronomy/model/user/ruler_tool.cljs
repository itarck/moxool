(ns astronomy.model.user.ruler-tool)


;; 尺子只能量天球点的距离，距离用角度表示
;; 尺子有几个状态：init，select1，select2

(def ruler-tool-1
  #:ruler-tool {:status :init
                :point1 {:db/id 1}
                :point2 {:db/id 2}})

(def schema {})

;; tx

(defn select1-tx [ruler1 point1]
  
  
  )

