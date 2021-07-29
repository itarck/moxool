(ns astronomy.view.user.crosshair-tool
  (:require
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [helix.core :refer [$ defnc]]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]
   [astronomy.component.tool :as c.tool]))


(def crosshair-tool-1
  #:crosshair-tool {:tool/name "crosshair-tool"
                    :tool/chinese-name "标注点工具"
                    :tool/icon "/image/moxool/crosshair-tool.jpg"
                    :tool/type :crosshair-tool

                    :tool/panels [:create-panel :query-panel :pull-panel :delete-panel]
                    :tool/current-panel :create-panel
                    :entity/type :crosshair-tool})


(defn CrosshairToolView [props {:keys [service-chan conn]}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        {:tool/keys [panels current-panel]} tool
        panel-props {:panels (vec (for [panel panels]
                                    {:name panel
                                     :onClick #(go (>! service-chan #:event {:action :astronomical-coordinate-tool/log
                                                                             :detail {:tool tool
                                                                                      :tool/panel panel}}))}))
                     :current-panel current-panel} ]
    [:div {:class "astronomy-righthand"}
     [:div {:class "astronomy-righthand-tool"}
      
      [:div.p-2
       [:div
        [:img {:src (:tool/icon tool)
               :class "astronomy-button"}]
        [:span {:style {:font-size "18px"
                        :font-weight "bold"}}
         (:tool/chinese-name tool)]]

       [:> mt/Grid {:container true :spacing 1}

        [:> mt/Grid {:item true :xs 12}
         [:> c.tool/PanelsComponent panel-props]]
        [:> mt/Grid {:item true :xs 12}
         [:span (str tool)]]]


    ;;    
       ]]]))

