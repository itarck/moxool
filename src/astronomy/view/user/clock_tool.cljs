(ns astronomy.view.user.clock-tool
  (:require
   [applied-science.js-interop :as j]
   [cljs-bean.core :refer [->js]]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [cuerdas.core :as cue]
   [goog.string :as gstring]
   [helix.core :refer [$]]
   [posh.reagent :as p]
   [reagent.core :as r]
   ["@material-ui/core" :as mt]
   [astronomy.model.clock :as m.clock]))


(defn ClockToolView [props {:keys [service-chan conn]}]
  (let [{:clock-tool/keys [steps-per-second step-interval clock] :as clock-tool} @(p/pull conn '[*] (:db/id props))
        clock @(p/pull conn '[*] (:db/id clock))
        {:clock/keys [time-in-days]} clock
        step-interval-in-chinese (case step-interval
                                   :minute "分"
                                   :hour "小时"
                                   :star-day "恒星日"
                                   :day "日")
        gen-click-step-interval (fn [step-interval]
                                  #(go (>! service-chan #:event{:action :clock-tool/change-step-interval
                                                                :detail {:clock-tool clock-tool
                                                                         :step-interval step-interval}})))
        parsed-time (m.clock/parse-time-in-days time-in-days)]

    [:div.p-2
     [:div
      [:img {:src (:tool/icon clock-tool)
             :class "astronomy-button"}]
      [:span {:style {:font-size "18px"
                      :font-weight "bold"}} "时钟"]]

     ($ mt/Grid {:container true :spacing 1}
        ($ mt/Grid {:item true :xs 12}
           ($ mt/Typography {:variant "subtitle2"}
              (let [{:keys [days minutes hours seconds]} parsed-time]
                (str "当前时间： 第"
                     days "天 "
                     hours ":"
                     minutes ":"
                     (gstring/format "%0.3f" seconds) ))))

        ($ mt/Grid {:item true :xs 12}
           ($ mt/Typography {:variant "subtitle2"}
              (str "播放速度： " steps-per-second " fps"))
           ($ mt/Slider
              {:style (->js {:color "#666"
                             :width "200px"})
               :value steps-per-second
               :onChange (fn [e value]
                           (go (>! service-chan #:event {:action :clock-tool/change-steps-per-second
                                                         :detail {:clock-tool clock-tool
                                                                  :steps-per-second value}})))
               :step 1 :min 1 :max 50 :marks true
               :getAriaValueText identity
               :aria-labelledby "discrete-slider-restrict"
               :valueLabelDisplay "auto"}))

        ($ mt/Grid {:item true :xs 12}
           ($ mt/Typography {:variant "subtitle2"} "时间步长：" step-interval-in-chinese)
           ($ mt/ButtonGroup {:size "small"}
              ($ mt/Button {:onClick (gen-click-step-interval :minute)
                            :variant (if (= step-interval :minute) "contained" "outlined")}
                 "分")
              ($ mt/Button {:onClick (gen-click-step-interval :hour)
                            :variant (if (= step-interval :hour) "contained" "outlined")}
                 "时")
              ($ mt/Button {:onClick (gen-click-step-interval :star-day)
                            :variant (if (= step-interval :star-day) "contained" "outlined")}
                 "恒星日")
              ($ mt/Button {:onClick (gen-click-step-interval :day)
                            :variant (if (= step-interval :day) "contained" "outlined")}
                 "日")))

        ($ mt/Grid {:item true :xs 12}
           ($ mt/Typography {:variant "subtitle2"} "动作")
           ($ mt/ButtonGroup {:size "small"}
              ($ mt/Button {:onClick #(go (>! service-chan #:event{:action :clock-tool/reset
                                                                   :detail {:clock-tool clock-tool
                                                                            :clock clock}}))} "重置")
              ($ mt/Button {:onClick #(go (>! service-chan #:event{:action :clock-tool/start
                                                                   :detail {:clock-tool clock-tool
                                                                            :clock clock}}))} "开始")
              ($ mt/Button {:onClick #(go (>! service-chan #:event{:action :clock-tool/stop
                                                                   :detail {:clock-tool clock-tool
                                                                            :clock clock}}))} "停止")
              ($ mt/Button {:onClick #(go (>! service-chan #:event{:action :clock-tool/next-step
                                                                   :detail {:clock-tool clock-tool
                                                                            :clock clock}}))} "下一步"))))]))

