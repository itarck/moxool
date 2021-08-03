(ns astronomy.view.user.astronomical-point-tool
  (:require
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [helix.core :refer [$ defnc]]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]
   [astronomy.component.tool :as c.tool]))

;; data

(def apt-tool-1
  #:astronomical-point-tool
   {:tool/name "astronomical-point-tool"
    :tool/chinese-name "标注点工具"
    :tool/icon "/image/moxool/crosshair-tool.jpg"
    :tool/type :astronomical-point-tool
    :tool/panels [:create-panel :query-panel :pull-panel :delete-panel]
    :tool/current-panel :create-panel
    :entity/type :astronomical-point-tool})

;; views

(defn AstronomicalPointCreatePanelView [props env]
  (let [{:keys [tool]} props]
    [:<>
     [:> c.tool/Title1Component {:title "操作"}]
     [:> c.tool/ButtonGroupComponent
      {:buttons [{:name "准备"
                  :selected true
                  :onClick println}
                 {:name "新建"
                  :onClick println}]}]]))

(defn AstronomicalPointQueryPanelView [props env]
  (let [{:keys [tool]} props]
    [:div
     [:p "query-panel"]
     [:span (str tool)]]))

(defn AstronomicalPointToolView [props {:keys [service-chan conn] :as env}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        {:tool/keys [panels current-panel]} tool
        panel-props {:panels (vec (for [panel panels]
                                    {:name panel
                                     :onClick #(go (>! service-chan #:event {:action :tool/change-panel
                                                                             :detail {:tool tool
                                                                                      :current-panel panel}}))}))
                     :current-panel current-panel}]
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
         [:> c.tool/PanelsComponent panel-props]]]

       [:> mt/Grid {:container true :spacing 1}
        (case current-panel
          :create-panel [AstronomicalPointCreatePanelView {:tool tool} env]
          :query-panel [AstronomicalPointQueryPanelView {:tool tool} env]
          [:div])]
       



    ;;    
       ]]]))

