(ns astronomy.model.user.astronomical-point-tool
  (:require))


;; 标记点工具，缩写apt-tool


(def apt-tool-1
  #:astronomical-point-tool
   {:tool/name "astronomical-point-tool"
    :tool/chinese-name "标注点工具"
    :tool/icon "/image/moxool/crosshair-tool.jpg"
    :tool/type :astronomical-point-tool
    :tool/panels [:create-panel :query-panel :pull-panel :delete-panel]
    :tool/current-panel :create-panel
    :entity/type :astronomical-point-tool})

;; tx 

(defn pull-point-tx [tool point]
  [{:db/id (:db/id tool)
    :astronomical-point-tool/pull-id (:db/id point) }])