(ns astronomy.tools.ppt-tool.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]
   [astronomy.tools.ppt-tool.m :as m.ppt-tool]))



(defn PPTHudView [props {:keys [service-chan conn]}]
  (let [ppt-tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        ppt-names (m.ppt-tool/sub-all-ppt-names conn)
        ppt-name (first (:ppt-tool/query-args ppt-tool))
        current-ppt (if ppt-name @(p/pull conn '[*] [:ppt/chinese-name ppt-name]) nil)
        current-page-index (:ppt/current-page current-ppt)
        count-pages (m.ppt-tool/count-pages current-ppt)
        current-page (m.ppt-tool/current-page current-ppt)]

    [:div {:class "astronomy-hud"}
     [:div {:class "astronomy-hud-content"}
      [:div.p-2
       [:> mt/Grid {:container true}
        [:> mt/Grid {:item true :xs 4}
         [:img {:src (:tool/icon ppt-tool)
                :class "astronomy-button"}]
         [:span {:style {:font-size "18px"
                         :font-weight "bold"
                         :margin "4px"}}
          (:tool/chinese-name ppt-tool)]]

        [:> mt/Grid {:item true :xs 7}
         [:> mt/Select {:value (first (:ppt-tool/query-args ppt-tool))
                        :onChange (fn [e]
                                    (let [new-value (j/get-in e [:target :value])]
                                      (go (>! service-chan
                                              #:event {:action :ppt-tool/change-query-args
                                                       :detail {:ppt-tool ppt-tool
                                                                :query-args [new-value]}}))))}
          (for [ppt-name ppt-names]
            ^{:key ppt-name}
            [:> mt/MenuItem {:value ppt-name} ppt-name])]]

        [:> mt/Grid {:item true :xs 1}
         [:> mt/Typography {:variant "subtitle1"} (str (inc current-page-index) " / " count-pages)]]]

       [:> mt/Divider]

       [:div.pt-2
        [:img {:src (:ppt-page/image-url current-page)
               :style {:width "100%"}}]]
       [:div.pt-2
        [:> mt/Grid {:container true}
         [:> mt/Grid {:item true :xs 10}]
         [:> mt/Grid {:item true
                      :xs 2}
          [:> mt/ButtonGroup {:size "small"}
           [:> mt/Button {:onClick #(go (>! service-chan #:event{:action :ppt-tool/prev-page
                                                                 :detail {:current-ppt current-ppt}}))
                          :variant "outlined"}
            "上一页"]
           [:> mt/Button {:onClick #(go (>! service-chan #:event{:action :ppt-tool/next-page
                                                                 :detail {:current-ppt current-ppt}}))
                          :variant "outlined"}
            "下一页"]]]]]]]]))