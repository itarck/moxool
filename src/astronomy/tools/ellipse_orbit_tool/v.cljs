(ns astronomy.tools.ellipse-orbit-tool.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]))



(defn EllipseOrbitToolView [{:keys [tool]} {:keys [conn service-chan]}]
  (let [eot @(p/pull conn '[*] (:db/id tool))
        ids @(p/q '[:find [?id ...]
                    :where [?id :entity/type :planet]]
                  conn)
        candidates (doall (mapv (fn [id] @(p/pull conn '[:db/id :planet/chinese-name] id)) ids))
        planet @(p/pull conn '[*] (get-in eot [:selector/selected :db/id]))
        orbit @(p/pull conn '[*] (get-in planet [:celestial/orbit :db/id]))
        {:ellipse-orbit/keys [semi-major-axis eccentricity inclination-in-degree
                              longitude-of-the-ascending-node-in-degree argument-of-periapsis-in-degree
                              mean-anomaly-in-degree]} orbit]
    
    [:div {:class "astronomy-righthand"}
     [:div {:class "astronomy-righthand-tool"}
      [:div.p-2
       [:div
        [:img {:src (:tool/icon eot)
               :class "astronomy-button"}]
        [:span {:style {:font-size "18px"
                        :font-weight "bold"}}
         (:tool/chinese-name eot)]]

       [:> mt/Grid {:item true :xs 12
                    :style {:margin-bottom "10px"}}
        [:> mt/Select {:value (get-in eot [:selector/selected :db/id])
                       :onChange (fn [e]
                                   (let [new-value (j/get-in e [:target :value])]
                                     (go (>! service-chan
                                             #:event {:action :selector/select
                                                      :detail {:selector tool
                                                               :selected new-value}}))))}
         (for [one candidates]
           ^{:key (:db/id one)}
           [:> mt/MenuItem {:value (:db/id one)}
            (or (:planet/chinese-name one)
                (:db/id one))])]]

       [:> mt/Grid {:item true :xs 12}
        [:> mt/Typography {:variant "subtitle2"} "显示轨道："
         [:span "否"]
         [:> mt/Switch
          {:color "default"
           :size "small"
           :checked (or (get-in orbit [:orbit/show?]) false)
           :onChange (fn [event]
                       (let [show? (j/get-in event [:target :checked])]
                         (go (>! service-chan #:event {:action :planet/show-orbit
                                                       :detail {:celestial planet
                                                                :show? show?}}))))}]
         [:span "是"]]
        [:> mt/Typography {:variant "subtitle2"} "显示辅助线："
         [:span "否"]
         [:> mt/Switch
          {:color "default"
           :size "small"
           :checked (or (get-in orbit [:orbit/show-helper-lines?]) false)
           :onChange (fn [event]
                       (let [show? (j/get-in event [:target :checked])]
                         (go (>! service-chan #:event {:action :ellipse-orbit-tool/change-show-helper-lines
                                                       :detail {:ellipse-orbit orbit
                                                                :show? show?}}))))}]
         [:span "是"]]]

       [:> mt/Grid {:item true :xs 12}
        [:> mt/Typography {:variant "subtitle2"} "轨道半长轴：" semi-major-axis " 光秒"]
        [:> mt/Slider
         {:style (clj->js {:color "#666"
                           :width "200px"})
          :value semi-major-axis
          :onChange (fn [e value]
                      (go (>! service-chan #:event{:action :ellipse-orbit-tool/set-attr
                                                   :detail {:ellipse-orbit-tool eot
                                                            :attr :ellipse-orbit/semi-major-axis
                                                            :value value}})))
          :step 40 :min 1 :max 1000 :marks true
          :getAriaValueText identity
          :aria-labelledby "discrete-slider-restrict"
          :valueLabelDisplay "auto"}]]

       [:> mt/Grid {:item true :xs 12}
        [:> mt/Typography {:variant "subtitle2"} "轨道离心率: " eccentricity]
        [:> mt/Slider
         {:style (clj->js {:color "#666"
                           :width "200px"})
          :value eccentricity
          :onChange (fn [e value]
                      (go (>! service-chan #:event{:action :ellipse-orbit-tool/set-attr
                                                   :detail {:ellipse-orbit-tool eot
                                                            :attr :ellipse-orbit/eccentricity
                                                            :value value}})))
          :step 0.05 :min 0 :max 1 :marks true
          :getAriaValueText identity
          :aria-labelledby "discrete-slider-restrict"
          :valueLabelDisplay "auto"}]]

       [:> mt/Grid {:item true :xs 12}
        [:> mt/Typography {:variant "subtitle2"} "轨道倾角: " inclination-in-degree " 度"]
        [:> mt/Slider
         {:style (clj->js {:color "#666"
                           :width "200px"})
          :value inclination-in-degree
          :onChange (fn [e value]
                      (go (>! service-chan #:event{:action :ellipse-orbit-tool/set-attr
                                                   :detail {:ellipse-orbit-tool eot
                                                            :attr :ellipse-orbit/inclination-in-degree
                                                            :value value}})))
          :step 5 :min 0 :max 90 :marks true
          :getAriaValueText identity
          :aria-labelledby "discrete-slider-restrict"
          :valueLabelDisplay "auto"}]]

       [:> mt/Grid {:item true :xs 12}
        [:> mt/Typography {:variant "subtitle2"} "升交点黄经: " longitude-of-the-ascending-node-in-degree " 度"]
        [:> mt/Slider
         {:style (clj->js {:color "#666"
                           :width "200px"})
          :value longitude-of-the-ascending-node-in-degree
          :onChange (fn [e value]
                      (go (>! service-chan #:event{:action :ellipse-orbit-tool/set-attr
                                                   :detail {:ellipse-orbit-tool eot
                                                            :attr :ellipse-orbit/longitude-of-the-ascending-node-in-degree
                                                            :value value}})))
          :step 5 :min -180 :max 180 :marks true
          :getAriaValueText identity
          :aria-labelledby "discrete-slider-restrict"
          :valueLabelDisplay "auto"}]]

       [:> mt/Grid {:item true :xs 12}
        [:> mt/Typography {:variant "subtitle2"} "近日点幅角: " argument-of-periapsis-in-degree " 度"]
        [:> mt/Slider
         {:style (clj->js {:color "#666"
                           :width "200px"})
          :value argument-of-periapsis-in-degree
          :onChange (fn [e value]
                      (go (>! service-chan #:event{:action :ellipse-orbit-tool/set-attr
                                                   :detail {:ellipse-orbit-tool eot
                                                            :attr :ellipse-orbit/argument-of-periapsis-in-degree
                                                            :value value}})))
          :step 5 :min 0 :max 360 :marks true
          :getAriaValueText identity
          :aria-labelledby "discrete-slider-restrict"
          :valueLabelDisplay "auto"}]]

       [:> mt/Grid {:item true :xs 12}
        [:> mt/Typography {:variant "subtitle2"} "平近点角（J2000）: " mean-anomaly-in-degree " 度"]
        [:> mt/Slider
         {:style (clj->js {:color "#666"
                           :width "200px"})
          :value mean-anomaly-in-degree
          :onChange (fn [e value]
                      (go (>! service-chan #:event{:action :ellipse-orbit-tool/set-attr
                                                   :detail {:ellipse-orbit-tool eot
                                                            :attr :ellipse-orbit/mean-anomaly-in-degree
                                                            :value value}})))
          :step 5 :min 0 :max 360 :marks true
          :getAriaValueText identity
          :aria-labelledby "discrete-slider-restrict"
          :valueLabelDisplay "auto"}]]]]]))