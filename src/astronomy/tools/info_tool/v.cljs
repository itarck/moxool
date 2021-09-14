(ns astronomy.tools.info-tool.v
  (:require
   [astronomy.tools.info-tool.m :as m.info-tool]))



(defn InfoToolView [{:keys [tool]} {:keys [conn]}]
  (let [info-tool (m.info-tool/sub-info-tool conn (:db/id tool))]
    [:div {:class "astronomy-righthand"}
     [:div {:class "astronomy-righthand-tool"}
      [:div.p-2
       [:div
        [:img {:src (:tool/icon info-tool)
               :class "astronomy-button"}]
        [:span {:style {:font-size "18px"
                        :font-weight "bold"}}
         (:tool/chinese-name info-tool)]]

       (if (:info-tool/object info-tool)
         [:div
          (str (:info-tool/object info-tool))]
         [:div
          "请点击场景中的物体"])]]]))