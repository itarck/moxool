(ns astronomy.component.tool
  (:require
   [cljs-bean.core :refer [bean ->js]]
   [helix.core :refer [$ defnc]]
   ["@material-ui/core" :as mt]))


;;  PanelsComponent 显示可选择的panel，输入的props如下
;; {:panels #js [#js {:name "create-panel"
;;                    :onClick println}
;;               #js {:name "create-panel"
;;                    :onClick println}]
;;  :currentPanel "create-panel"}

(defn get-panel-label [panel-name]
  (get {"create-panel" "新建"
        "query-panel" "查询"
        "pull-panel" "读取"
        "delete-panel" "删除"}
       panel-name))

(defnc PanelsComponent [{:keys [classes panels currentPanel] :as props}]
  {:wrap [((mt/withStyles (->js {:root {:margin-top "4px"}
                                 :button  {:padding "4px 10px"}})))]}
  (let [bclasses (bean classes)]
    ($ mt/ButtonGroup {:size "medium"
                       :className (:root bclasses)}
       (for [panel (seq panels)]
         (let [{:keys [name onClick]} (bean panel)]
           ($ mt/Button {:key name
                         :onClick onClick
                         :variant (if (= currentPanel name) "contained" "outlined")
                         :className (:button bclasses)}
              (get-panel-label name)))))))
