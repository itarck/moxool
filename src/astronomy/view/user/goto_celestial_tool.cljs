(ns astronomy.view.user.goto-celestial-tool
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [goog.string :as gstring]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]
   [astronomy.model.celestial :as m.celestial]))


(defn GotoCelestialToolView [props {:keys [service-chan conn]}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        target @(p/pull conn '[{:celestial/orbit [*]
                                :celestial/spin [*]} *] (get-in tool [:tool/target :db/id]))
        candidate-id-and-names (sort-by first @(p/q m.celestial/query-all-id-and-chinese-name conn))]
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
         [:> mt/Select {:value (-> target :db/id)
                        :onChange (fn [e]
                                    (let [new-id (j/get-in e [:target :value])]
                                      (go (>! service-chan
                                              #:event {:action :goto-celestial-tool/goto
                                                       :detail {:new-celestial-id new-id
                                                                :goto-celestial-tool tool}}))))}
          (for [[id name] candidate-id-and-names]
            ^{:key id}
            [:> mt/MenuItem {:value id} name])]]
        [:> mt/Grid {:item true :xs 12}
         [:> mt/Typography {:variant "subtitle1"} (str "星球类型："
                                                       (case (:entity/type target)
                                                         :star "恒星"
                                                         :planet "行星"
                                                         :satellite "卫星"))]
         [:> mt/Typography {:variant "subtitle1"} (str "星球平均半径：" (gstring/format "%0.6f" (:celestial/radius target)) "光秒")]
         (when-let [p (or
                       (get-in target [:celestial/orbit :ellipse-orbit/semi-major-axis])
                       (get-in target [:celestial/orbit :circle-orbit/radius]))]
           [:> mt/Typography {:variant "subtitle1"} (str "公转平均距离：" (gstring/format "%0.3f" p) "光秒")])
         (when-let [p (get-in target [:celestial/orbit :orbit/period])]
           [:> mt/Typography {:variant "subtitle1"} (str "公转周期："
                                                         (if (> p 500)
                                                           (str (gstring/format "%0.3f" (/ p 365)) "年")
                                                           (str (gstring/format "%0.3f" p) "日")))])
         (when-let [p (get-in target [:celestial/spin :spin/period])]
           [:> mt/Typography {:variant "subtitle1"} (str "自转周期：" (gstring/format "%0.3f" p) "日")])]

        [:> mt/Grid {:item true :xs 12}
         (when (= :planet (:entity/type target))
           [:> mt/Typography {:variant "subtitle1"} "显示公转轨道："
            [:span "否"]
            [:> mt/Switch
             {:color "default"
              :size "small"
              :checked (or (get-in target [:celestial/orbit :orbit/show?]) false)
              :onChange (fn [event]
                          (let [show? (j/get-in event [:target :checked])]
                            (go (>! service-chan #:event {:action :goto-celestial-tool/show-orbit
                                                          :detail {:celestial target
                                                                   :show? show?}}))))}]
            [:span "是"]])

         [:> mt/Typography {:variant "subtitle1"} "显示赤道平面："
          [:span "否"]
          [:> mt/Switch
           {:color "default"
            :size "small"
            :checked (or (get-in target [:celestial/spin :spin/show-helper?]) false)
            :onChange (fn [event]
                        (let [show? (j/get-in event [:target :checked])]
                          (go (>! service-chan #:event {:action :goto-celestial-tool/show-spin-helper
                                                        :detail {:celestial target
                                                                 :show? show?}}))))}]
          [:span "是"]]

         (when (= :star (:entity/type target))
           [:<>
            [:> mt/Typography {:variant "subtitle1"} "所有行星轨道"]
            [:> mt/ButtonGroup {:size "small"}
             [:> mt/Button {:onClick #(go (>! service-chan #:event{:action :goto-celestial-tool/show-all-planet-orbit
                                                                   :detail {:celestial target
                                                                            :show? true}}))} "显示"]
             [:> mt/Button {:onClick #(go (>! service-chan #:event{:action :goto-celestial-tool/show-all-planet-orbit
                                                                   :detail {:celestial target
                                                                            :show? false}}))} "隐藏"]]])


;; 
         ]]]]]))