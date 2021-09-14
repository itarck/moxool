(ns astronomy.tools.terrestrial-coordinate-tool.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   [goog.string :as gstring]
   ["@material-ui/core" :as mt]
   [helix.core :refer [$]]
   [shu.arithmetic.number :as number]
   [astronomy.tools.terrestrial-coordinate-tool.m :as m.terrestrial-coordinate-tool]))


(defn radius-format [n]
  (cond
    (> n 1000) (gstring/format "%0.1f" n)
    (> n 10) (gstring/format "%0.2f" n)
    :else (gstring/format "%0.3f" n)))


(defn TerrestrialCoordinateToolView [{:keys [astro-scene] :as props} {:keys [service-chan conn]}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        {:terrestrial-coordinate-tool/keys [query-args]} tool
        query-args-candidates (m.terrestrial-coordinate-tool/sub-query-args-candidates conn tool)]
    ;; (println "TerrestrialCoordinateToolView: " tool)
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
                                              #:event {:action :terrestrial-coordinate-tool/change-query-args
                                                       :detail {:tool tool
                                                                :query-args [new-value]}}))))}
          (for [name (concat ["未选择"] query-args-candidates)]
            ^{:key name}
            [:> mt/MenuItem {:value name} name])]]

        (when (and (seq query-args) (not= (first query-args) "未选择"))
          (let [terrestrial-coordinate-id (first (get-in tool [:terrestrial-coordinate-tool/query-result]))
                terrestrial-coordinate @(p/pull conn '[*] terrestrial-coordinate-id)
                {:terrestrial-coordinate/keys [show-latitude? show-longitude? show-latitude-0? show-regression-line?
                                               show-longitude-0? radius]} terrestrial-coordinate]
            [:<>
             [:> mt/Grid {:item true :xs 6}
              [:> mt/Typography {:variant "subtitle2"} "设为系统参考系"]]
             [:> mt/Grid {:item true :xs 6}
              [:> mt/ButtonGroup {:size "small"}
               [:> mt/Button {:onClick #(go (>! service-chan #:event{:action :astro-scene/change-coordinate
                                                                     :detail {:astro-scene astro-scene
                                                                              :coordinate terrestrial-coordinate}}))} "设置"]]]

             [:> mt/Grid {:item true :xs 6}
              [:> mt/Typography {:variant "subtitle2"} "显示经度"]]
             [:> mt/Grid {:item true :xs 6}
              [:span "否"]
              [:> mt/Switch {:color "default"
                             :size "small"
                             :checked (or show-longitude? false)
                             :onChange (fn [event]
                                         (let [show? (j/get-in event [:target :checked])]
                                           (go (>! service-chan #:event {:action :terrestrial-coordinate-tool/change-show-longitude
                                                                         :detail {:terrestrial-coordinate terrestrial-coordinate
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
                                           (go (>! service-chan #:event {:action :terrestrial-coordinate-tool/change-show-latitude
                                                                         :detail {:terrestrial-coordinate terrestrial-coordinate
                                                                                  :show? show?}}))))}]
              [:span "是"]]


             [:> mt/Grid {:item true :xs 6}
              [:> mt/Typography {:variant "subtitle2"} "显示赤道"]]
             [:> mt/Grid {:item true :xs 6}
              [:span "否"]
              [:> mt/Switch {:color "default"
                             :size "small"
                             :checked show-latitude-0?
                             :onChange (fn [event]
                                         (let [show? (j/get-in event [:target :checked])]
                                           (go (>! service-chan #:event {:action :terrestrial-coordinate-tool/change-show-latitude-0
                                                                         :detail {:terrestrial-coordinate terrestrial-coordinate
                                                                                  :show? show?}}))))}]
              [:span "是"]]

             [:> mt/Grid {:item true :xs 6}
              [:> mt/Typography {:variant "subtitle2"} "显示回归线"]]
             [:> mt/Grid {:item true :xs 6}
              [:span "否"]
              [:> mt/Switch {:color "default"
                             :size "small"
                             :checked show-regression-line?
                             :onChange (fn [event]
                                         (let [show? (j/get-in event [:target :checked])]
                                           (go (>! service-chan #:event {:action :terrestrial-coordinate-tool/change-show-regression-line
                                                                         :detail {:terrestrial-coordinate terrestrial-coordinate
                                                                                  :show? show?}}))))}]
              [:span "是"]]

             [:> mt/Grid {:item true :xs 6}
              [:> mt/Typography {:variant "subtitle2"} "显示本初子午线"]]
             [:> mt/Grid {:item true :xs 6}
              [:span "否"]
              [:> mt/Switch {:color "default"
                             :size "small"
                             :checked show-longitude-0?
                             :onChange (fn [event]
                                         (let [show? (j/get-in event [:target :checked])]
                                           (go (>! service-chan #:event {:action :terrestrial-coordinate-tool/change-show-longitude-0
                                                                         :detail {:terrestrial-coordinate terrestrial-coordinate
                                                                                  :show? show?}}))))}]
              [:span "是"]]


             [:> mt/Grid {:item true :xs 6}
              [:> mt/Typography {:variant "subtitle2"} "天球半径"]]
             [:> mt/Grid {:item true :xs 6}
              [:> mt/Typography {:variant "subtitle2"} (str (radius-format radius) "光秒")]]
             [:> mt/Grid {:item true :xs 12}
              ($ mt/Slider
                 {:style (clj->js {:color "#666"
                                   :width "200px"})
                  :value (number/log 10 radius)
                  :onChange (fn [e value]
                              (go (>! service-chan #:event {:action :terrestrial-coordinate-tool/change-radius
                                                            :detail {:terrestrial-coordinate terrestrial-coordinate
                                                                     :radius (number/pow 10 value)}})))
                  :step 0.02 :min -3 :max 5 :marks true
                  :getAriaValueText #(number/pow 10 %)
                  :valueLabelFormat (fn [n] (radius-format (number/pow 10 n)))
                  :aria-labelledby "discrete-slider-restrict"
                  :valueLabelDisplay "auto"})]


            ;;  
             ]))]]]]))
