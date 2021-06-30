(ns astronomy.view.user.horizontal-coordinate-tool
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]))



(defn HorizontalCoordinateToolView [props {:keys [service-chan conn]}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        horizontal-coordinate @(p/pull conn '[*] (get-in tool [:horizontal-coordinate-tool/target :db/id]))
        {:horizontal-coordinate/keys [radius show-latitude? show-longitude? show-horizontal-plane? show-compass?]} horizontal-coordinate
        spaceship-camera-control @(p/pull conn '[*] (get-in props [:spaceship-camera-control :db/id]))]
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
         [:> mt/Typography {:variant "subtitle1"}
          (if (= :surface-control (:spaceship-camera-control/mode spaceship-camera-control))
            "当前位于地表，可使用地平坐标系"
            "当前不在地表，无法使用地平坐标系")]]
        [:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "显示经度"]]
        [:> mt/Grid {:item true :xs 6}
         [:span "否"]
         [:> mt/Switch {:color "default"
                        :size "small"
                        :checked (or show-longitude? false)
                        :onChange (fn [event]
                                    (let [show? (j/get-in event [:target :checked])]
                                      (go (>! service-chan #:event {:action :horizontal-coordinate-tool/change-show-longitude
                                                                    :detail {:tool tool
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
                                      (go (>! service-chan #:event {:action :horizontal-coordinate-tool/change-show-latitude
                                                                    :detail {:tool tool
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
                                      (go (>! service-chan #:event {:action :horizontal-coordinate-tool/change-show-compass
                                                                    :detail {:tool tool
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
                                      (go (>! service-chan #:event {:action :horizontal-coordinate-tool/change-show-horizontal-plane
                                                                    :detail {:tool tool
                                                                             :show? show?}}))))}]
         [:span "是"]]
         
         #_[:> mt/Grid {:item true :xs 6}
         [:> mt/Typography {:variant "subtitle2"} "天球半径"]]
         #_[:> mt/Grid {:item true :xs 6}
          ($ mt/Slider
             {:style (clj->js {:color "#666"
                               :width "100px"})
              :value radius
              :onChange (fn [e value]
                          (go (>! service-chan #:event {:action :horizontal-coordinate-tool/change-radius
                                                        :detail {:tool tool
                                                                 :radius value}})))
              :step 0.001 :min 0.001 :max 0.01 :marks true
              :getAriaValueText identity
              :aria-labelledby "discrete-slider-restrict"
              :valueLabelDisplay "auto"})]
         
         ]]]]))