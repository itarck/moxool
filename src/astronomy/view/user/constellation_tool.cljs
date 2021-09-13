(ns astronomy.view.user.constellation-tool
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   [astronomy.model.user.constellation-tool :as m.constel-tool]
   ["@material-ui/core" :as mt]))


(defn ConstellationToolView [props {:keys [service-chan conn]}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        query-types (m.constel-tool/get-query-types)
        selected-constellation-ids (m.constel-tool/sub-selected-contellation-ids conn tool)
        constellation-candinates (m.constel-tool/sub-candinates-by-query-type conn (:constellation-tool/query-type tool))]
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
        (let [count-selector (count selected-constellation-ids)]
          [:<>
           [:> mt/Grid {:item true :xs 12}
            [:> mt/Typography {:variant "subtitle1"} (str "选择器: " "选中" count-selector "个")]]

           [:> mt/Grid {:item true :xs 6}
            [:> mt/Select {:value (:constellation-tool/query-type tool)
                           :onChange (fn [e]
                                       (let [new-value (keyword (j/get-in e [:target :value]))]
                                         (go (>! service-chan
                                                 #:event {:action :constellation-tool/change-query-type
                                                          :detail {:constellation-tool tool
                                                                   :new-query-type new-value}}))))}
             (for [query-type query-types]
               ^{:key (:value query-type)}
               [:> mt/MenuItem {:value (:value query-type)} (:name query-type)])]]

           (when (#{:one-by-name :by-group} (:constellation-tool/query-type tool))
             [:> mt/Grid {:item true :xs 5}
              [:> mt/Select {:value (or (first (:constellation-tool/query-args tool))
                                        "未选择")
                             :onChange (fn [e]
                                         (let [new-value (j/get-in e [:target :value])]
                                           (go (>! service-chan
                                                   #:event {:action :constellation-tool/change-query-args
                                                            :detail {:constellation-tool tool
                                                                     :query-args [new-value]}}))))}
               (for [name constellation-candinates]
                 ^{:key name}
                 [:> mt/MenuItem {:value name} name])]])])]

        [:> mt/Grid {:container true :spacing 1}
         [:> mt/Grid {:item true :xs 6}
          [:> mt/Typography {:variant "subtitle1"} "显示星座线"]]
         [:> mt/Grid {:item true :xs 6}
          [:> mt/ButtonGroup {:size "small"}
           [:> mt/Button {:onClick #(go (>! service-chan #:event{:action :constellation-tool/show-lines
                                                                 :detail {:tool tool
                                                                          :constellation-ids selected-constellation-ids
                                                                          :show? true}}))} "显示"]
           [:> mt/Button {:onClick #(go (>! service-chan #:event{:action :constellation-tool/show-lines
                                                                 :detail {:tool tool
                                                                          :constellation-ids selected-constellation-ids
                                                                          :show? false}}))} "隐藏"]]]
         [:> mt/Grid {:item true :xs 6}
          [:> mt/Typography {:variant "subtitle1"} "显示星座名"]]
         [:> mt/Grid {:item true :xs 6}
          [:> mt/ButtonGroup {:size "small"}
           [:> mt/Button {:onClick #(go (>! service-chan #:event{:action :constellation-tool/show-name
                                                                 :detail {:tool tool
                                                                          :constellation-ids selected-constellation-ids
                                                                          :show? true}}))} "显示"]
           [:> mt/Button {:onClick #(go (>! service-chan #:event{:action :constellation-tool/show-name
                                                                 :detail {:tool tool
                                                                          :constellation-ids selected-constellation-ids
                                                                          :show? false}}))} "隐藏"]]]]
        

 
        ]]]))

