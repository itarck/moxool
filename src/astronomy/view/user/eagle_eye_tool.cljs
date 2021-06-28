(ns astronomy.view.user.eagle-eye-tool
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   [helix.core :refer [defnc $] :as h]
   ["@material-ui/core" :as mt]))



(defn EagleEyeToolView [props {:keys [service-chan conn]}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        spaceship-camera-control @(p/pull conn '[*] (get-in props [:spaceship-camera-control :db/id]))
        {:spaceship-camera-control/keys [mode zoom]} spaceship-camera-control]
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
          (when-not (= :surface-control mode)
            "当前不在地表，请先使用飞船工具降落地面")]]

        (when (= :surface-control mode)
          [:<>
           [:> mt/Grid {:item true :xs 6}
            [:> mt/Typography {:variant "subtitle1"} (str "放大系数：" zoom)]]
           [:> mt/Grid {:item true :xs 10}
            ($ mt/Slider
               {:style (clj->js {:color "#666"
                                 :width "200px"})
                :value zoom
                :onChange (fn [e value]
                            (go (>! service-chan #:event {:action :spaceship-camera-control/change-zoom
                                                          :detail {:spaceship-camera-control-id (:db/id tool)
                                                                   :zoom value}})))
                :step 0.2 :min 0.4 :max 4 :marks true
                :getAriaValueText identity
                :aria-labelledby "discrete-slider-restrict"
                :valueLabelDisplay "auto"})]])]]]]))