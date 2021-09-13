(ns astronomy.model.user.ruler-tool
  (:require
   [shu.astronomy.celestial-coordinate :as shu.cc]
   [astronomy.objects.astronomical-point.m :as m.apt]))


;; 尺子只能量天球点的距离，距离用角度表示
;; 尺子有几个状态：init，select1，select2


;; sample data

(def apt-1 (m.apt/astronomical-point 0 0))
(def apt-2 (m.apt/astronomical-point 30 30))

(def ruler-tool-1
  #:ruler-tool {:db/id -1000
                :status :init
                :point1 apt-1
                :point2 apt-2
                :object/scene [:scene/name "solar"]
                :tool/name "ruler-tool"
                :tool/chinese-name "天球尺"
                :tool/icon "/image/moxool/terrestrial-coordinate.jpg"
                :tool/type :ruler-tool
                :entity/type :ruler-tool})


(def schema {})

;; transform

(defn distance-in-degree [ruler1]
  (let [{:ruler-tool/keys [point1 point2]} ruler1
        cc1 (shu.cc/celestial-coordinate (:astronomical-point/longitude point1)
                                         (:astronomical-point/latitude point1))
        cc2 (shu.cc/celestial-coordinate (:astronomical-point/longitude point2)
                                         (:astronomical-point/latitude point2))]
    (shu.cc/distance-in-degree cc1 cc2)))

;; tx

(defn change-select1-tx [ruler1 point1]
  [{:db/id (:db/id ruler1)
    :ruler-tool/status :select1
    :ruler-tool/point1 point1}])


(defn change-select2-tx [ruler1 point2]
  [{:db/id (:db/id ruler1)
    :ruler-tool/status :select2
    :ruler-tool/point2 point2}])


(defn change-status-tx [ruler1 status]
  [{:db/id (:db/id ruler1)
    :ruler-tool/status status}])