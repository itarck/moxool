(ns astronomy.space.tool.v
  (:require
   [posh.reagent :as p]))



(defn ToolView [{:keys [tool]} {:keys [conn]}]
  (let [tool @(p/pull conn '[*] (:db/id tool))]
    [:div {:class "astronomy-righthand"}
     [:div {:class "astronomy-righthand-tool"}
      [:div.p-2
       [:div
        [:img {:src (:tool/icon tool)
               :class "astronomy-button"}]
        [:span {:style {:font-size "18px"
                        :font-weight "bold"}}
         (:tool/chinese-name tool)]]

       ]]]))