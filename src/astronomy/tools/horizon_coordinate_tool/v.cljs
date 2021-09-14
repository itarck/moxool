(ns astronomy.tools.horizon-coordinate-tool.v
  (:require
   [applied-science.js-interop :as j]
   [helix.core :refer [$]]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]
   [astronomy.tools.horizon-coordinate-tool.m :as m.horizon-coordinate-tool]))



(defn HorizonCoordinateToolView [{:keys [astro-scene] :as props} {:keys [service-chan conn]}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        {:horizon-coordinate-tool/keys [query-args]} tool
        query-args-candidates (m.horizon-coordinate-tool/sub-query-args-candidates conn tool)]
    ;; (println "HorizonCoordinateToolView: " tool query-args-candidates)
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
         [:> mt/Select {:value (or (first query-args) "未选择")
                        :onChange (fn [e]
                                    (let [new-value (j/get-in e [:target :value])]
                                      (go (>! service-chan
                                              #:event {:action :horizon-coordinate/change-query-args
                                                       :detail {:tool tool
                                                                :query-args [new-value]}}))))}
          (for [name (concat ["未选择"] query-args-candidates)]
            ^{:key name}
            [:> mt/MenuItem {:value name} name])]]

        (when (and (seq query-args) (not= (first query-args) "未选择"))
          (let [horizon-coordinate-id (first (get-in tool [:horizon-coordinate-tool/query-result]))
                horizon-coordinate @(p/pull conn '[*] horizon-coordinate-id)
                {:horizon-coordinate/keys [radius show-latitude? show-longitude? show-horizontal-plane? show-compass?]} horizon-coordinate]
            ;; (println "HorizonCoordinateToolView: " horizon-coordinate)
            [:<>
             [:> mt/Grid {:item true :xs 6}
              [:> mt/Typography {:variant "subtitle2"} "设为系统参考系"]]
             [:> mt/Grid {:item true :xs 6}
              [:> mt/ButtonGroup {:size "small"}
               [:> mt/Button {:onClick #(go (>! service-chan #:event{:action :astro-scene/change-coordinate
                                                                     :detail {:astro-scene astro-scene
                                                                              :coordinate horizon-coordinate}}))} "设置"]]]

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
                  :valueLabelDisplay "auto"})]

            ;;  
             ]))]]]]))