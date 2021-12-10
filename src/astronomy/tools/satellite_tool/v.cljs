(ns astronomy.tools.satellite-tool.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [goog.string :as gstring]
   [posh.reagent :as p]
   [helix.core :refer [$]]
   ["@material-ui/core" :as mt]
   [astronomy.objects.satellite.m :as satellite.m]))


(defn SatelliteToolView [props {:keys [service-chan conn]}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        target @(p/pull conn '[{:celestial/orbit [*]
                                :celestial/spin [*]} *] (get-in tool [:tool/target :db/id]))
        in-scene? (satellite.m/in-scene? target)
        candidate-id-and-names (sort-by first @(p/q satellite.m/query-all-id-and-chinese-name conn))
        {:celestial/keys [scale] :or {scale 1}} target]
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
                                    (let [new-id (j/get-in e [:target :value])
                                          event #:event {:action :satellite-tool/change-target
                                                         :detail {:new-satellite-id new-id
                                                                  :satellite-tool tool}}]
                                      (go (>! service-chan event))))}
          (for [[id name] candidate-id-and-names]
            ^{:key id}
            [:> mt/MenuItem {:value id} name])]]
        [:> mt/Grid {:item true :xs 12}
         [:> mt/Typography {:variant "subtitle1"} (str "星球类型：卫星")]
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
         [:> mt/Typography {:variant "subtitle1"} "显示星体："
          [:span "否"]
          [:> mt/Switch
           {:color "default"
            :size "small"
            :checked in-scene?
            :onChange (fn [event]
                        (go (>! service-chan #:event {:action :satellite/change-in-scene?
                                                      :detail {:satellite target
                                                               :in-scene? (not in-scene?)}})))}]
          [:span "是"]]


         [:> mt/Typography {:variant "subtitle1"} "显示公转轨道："
          [:span "否"]
          [:> mt/Switch
           {:color "default"
            :size "small"
            :checked (or (get-in target [:celestial/orbit :orbit/show?]) false)
            :onChange (fn [event]
                        (let [show? (j/get-in event [:target :checked])]
                          (go (>! service-chan #:event {:action :satellite/change-show-orbit
                                                        :detail {:celestial target
                                                                 :show? show?}}))))}]
          [:span "是"]]


         (when (= (:satellite/name target) "moon")
           [:> mt/Typography {:variant "subtitle1"} "显示辅助线："
            [:span "否"]
            [:> mt/Switch
             {:color "default"
              :size "small"
              :checked (or (get-in target [:celestial/orbit :moon-orbit/show-helper-lines?]) false)
              :onChange (fn [event]
                          (let [show? (j/get-in event [:target :checked])]
                            (go (>! service-chan #:event {:action :satellite/show-moon-orbit-helper-lines?
                                                          :detail {:celestial target
                                                                   :show? show?}}))))}]
            [:span "是"]])]


;; 
        ]]]]))