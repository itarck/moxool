(ns astronomy.view.user.horizon-coordinate-tool
  (:require
   [applied-science.js-interop :as j]
   [helix.core :refer [$]]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]))



(defn HorizonCoordinateToolView [props {:keys [service-chan conn]}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        horizon-coordinate @(p/pull conn '[*] (get-in tool [:tool/query-one-result]))
        {:horizon-coordinate/keys [radius show-latitude? show-longitude? show-horizontal-plane? show-compass?]} horizon-coordinate]
    ;; (println "HorizonCoordinateToolView: " tool)
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
        #_[:> mt/Grid {:item true :xs 12}
         [:> mt/Select {:value (or (first (:tool/query-args tool))
                                   "未选择")
                        :onChange (fn [e]
                                    (let [new-value (j/get-in e [:target :value])]
                                      (go (>! service-chan
                                              #:event {:action :horizon-coordinate/change-query-args
                                                       :detail {:tool tool
                                                                :query-args [new-value]}}))))}
          (for [name chinese-names]
            ^{:key name}
            [:> mt/MenuItem {:value name} name])]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "设为系统参考系"]]
        [:> mt/Grid {:item true :xs 6}
         [:> mt/ButtonGroup {:size "small"}
          [:> mt/Button {:onClick #(go (>! service-chan #:event{:action :horizon-coordinate/set-scene-reference
                                                                :detail {:horizon-coordinate horizon-coordinate}}))} "设置"]]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示经度"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or show-longitude? false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :horizon-coordinate/change-show-longitude
                                                                    :detail {:horizon-coordinate horizon-coordinate
                                                                             :show? show?}}))))}]
         [:span "是"]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示纬度"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or show-latitude? false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :horizon-coordinate/change-show-latitude
                                                                    :detail {:horizon-coordinate horizon-coordinate
                                                                             :show? show?}}))))}]
         [:span "是"]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示指南针"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or show-compass? false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :horizon-coordinate/change-show-compass
                                                                    :detail {:horizon-coordinate horizon-coordinate
                                                                             :show? show?}}))))}]
         [:span "是"]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示地平面"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or show-horizontal-plane? false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :horizon-coordinate/change-show-horizontal-plane
                                                                    :detail {:horizon-coordinate horizon-coordinate
                                                                             :show? show?}}))))}]
         [:span "是"]]

        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "天球半径"]]
        [:> mt/Grid {:item true :xs 6}
         ($ mt/Slider
            {:style (clj->js {:color "#666"
                              :width "100px"})
             :value radius
             :onChange (fn [e value]
                         (go (>! service-chan #:event {:action :horizon-coordinate/change-radius
                                                       :detail {:horizon-coordinate horizon-coordinate
                                                                :radius value}})))
             :step 0.0001 :min 0.0001 :max 0.005 :marks true
             :getAriaValueText identity
             :aria-labelledby "discrete-slider-restrict"
             :valueLabelDisplay "auto"})]]]]]))