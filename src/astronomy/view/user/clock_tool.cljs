(ns astronomy.view.user.clock-tool
  (:require
   [applied-science.js-interop :as j]
   [cljs-bean.core :refer [->js]]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [helix.core :refer [$]]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.clock :as m.clock]
   [astronomy.model.horizon-coordinate :as m.hc]))


(defn ClockToolView [props {:keys [service-chan conn]}]
  (let [clock-tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        scene-coordinate (m.astro-scene/sub-scene-coordinate conn (get-in props [:astro-scene]))
        {:clock-tool/keys [step-interval clock]} clock-tool
        clock @(p/pull conn '[*] (:db/id clock))
        {:clock/keys [time-in-days]} clock
        step-interval-in-chinese (case step-interval
                                   :minute "分"
                                   :hour "小时"
                                   :star-day "恒星日"
                                   :30day "月"
                                   :day "日"
                                   :year "年"
                                   :100year "百年")
        gen-click-step-interval (fn [step-interval]
                                  #(go (>! service-chan #:event{:action :clock-tool/change-step-interval
                                                                :detail {:clock-tool clock-tool
                                                                         :step-interval step-interval}})))]

    [:div {:class "astronomy-righthand"}
     [:div {:class "astronomy-righthand-tool"}
      [:div.p-2
       [:div
        [:img {:src (:tool/icon clock-tool)
               :class "astronomy-button"}]
        [:span {:style {:font-size "18px"
                        :font-weight "bold"}} (:tool/chinese-name clock-tool)]]

       [:> mt/Grid {:container true :spacing 1}

        (if (= (:coordinate/type scene-coordinate) :horizon-coordinate)
          [:<>
           ($ mt/Grid {:item true :xs 4}
              ($ mt/Typography {:variant "subtitle2"}
                 "本地时间： "))
           ($ mt/Grid {:item true :xs 8}
              ($ mt/Typography {:variant "subtitle2"}
                 (let [local-time (m.hc/cal-local-time scene-coordinate time-in-days)]
                   (m.clock/utc-format-string local-time))))]


          ($ mt/Grid {:item true :xs 12}
             ($ mt/Typography {:variant "subtitle2"}
                (str "世界时间： " (m.clock/utc-format-string time-in-days)))))

        #_($ mt/Grid {:item true :xs 12}
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
              ($ mt/Button {:onClick (gen-click-step-interval :day)
                            :variant (if (= step-interval :day) "contained" "outlined")}
                 "日")
              ($ mt/Button {:onClick (gen-click-step-interval :30day)
                            :variant (if (= step-interval :30day) "contained" "outlined")}
                 "月")
              ($ mt/Button {:onClick (gen-click-step-interval :year)
                            :variant (if (= step-interval :year) "contained" "outlined")}
                 "年"))
           ($ mt/ButtonGroup {:size "small"
                              :style {:margin-top "4px"}}
              ($ mt/Button {:onClick (gen-click-step-interval :100year)
                            :variant (if (= step-interval :100year) "contained" "outlined")}
                 "百年")))

        ($ mt/Grid {:item true :xs 12}
           ($ mt/Typography {:variant "subtitle2"} "动作")
           ($ mt/ButtonGroup {:size "small"}
              ($ mt/Button {:onClick #(go (>! service-chan #:event{:action :clock-tool/prev-step
                                                                   :detail {:clock-tool clock-tool
                                                                            :clock clock}}))} "上一步")
              ($ mt/Button {:onClick #(go (>! service-chan #:event{:action :clock-tool/start
                                                                   :detail {:clock-tool clock-tool
                                                                            :clock clock}}))} "开始")
              ($ mt/Button {:onClick #(go (>! service-chan #:event{:action :clock-tool/stop
                                                                   :detail {:clock-tool clock-tool
                                                                            :clock clock}}))} "停止")
              ($ mt/Button {:onClick #(go (>! service-chan #:event{:action :clock-tool/next-step
                                                                   :detail {:clock-tool clock-tool
                                                                            :clock clock}}))} "下一步")))]]]]))

