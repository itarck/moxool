(ns astronomy.view.user.astronomical-point-tool
  (:require
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [helix.core :refer [$ defnc]]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]
   [astronomy.component.tool :as c.tool]))


(defn AstronomicalPointCreatePanelView [props env]
  (let [{:keys [tool]} props]
    [:div
     [:p "create-panel"]
     [:span (str tool)]]))

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
         [:> c.tool/PanelsComponent panel-props]]
        [:> mt/Grid {:item true :xs 12}
         (case current-panel
           :create-panel [AstronomicalPointCreatePanelView {:tool tool} env]
           :query-panel [AstronomicalPointQueryPanelView {:tool tool} env]
           [:div])]]


    ;;    
       ]]]))

