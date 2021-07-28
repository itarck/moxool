(ns astronomy.view.user.spaceship-camera-control
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   [helix.core :refer [defnc $]]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   [astronomy.component.camera-controls :as c.camera-controls]))


;; object view

(defn SpaceshipCameraControlView [props {:keys [conn dom-atom]}]
  (let [scc @(p/pull conn '[*] (get-in props [:spaceship-camera-control :db/id]))
        {:spaceship-camera-control/keys [mode]} scc
        props (m.spaceship/cal-component-props scc mode)]
    [:> c.camera-controls/CameraControlsComponent
     (merge {:domAtom dom-atom} props)]))



(defn SpaceshipCameraToolView [props {:keys [service-chan conn dom-atom]}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        {:spaceship-camera-control/keys [mode zoom]} tool
        mode-and-names [[:orbit-mode "轨道运动模式"]
                        [:static-mode "环顾模式"]]]
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
         [:> mt/Typography {:variant "subtitle1"} "当前模式："]
         [:> mt/Select {:value mode
                        :onChange (fn [e]
                                    (let [new-mode (j/get-in e [:target :value])
                                          position (c.camera-controls/get-camera-position (:spaceship-camera-control @dom-atom))
                                          direction (c.camera-controls/get-camera-direction (:camera @dom-atom))]
                                      (go (>! service-chan
                                              #:event {:action :spaceship-camera-control/change-mode
                                                       :detail {:spaceship-camera-control tool
                                                                :new-mode (keyword new-mode)
                                                                :direction (vec direction)
                                                                :position (vec position)}}))))}
          (for [[mode mode-name] mode-and-names]
            ^{:key mode}
            [:> mt/MenuItem {:value mode} mode-name])]]]


       [:> mt/Grid {:item true :xs 6}
        [:> mt/Typography {:variant "subtitle1"} (str "放大系数：" zoom)]]
       [:> mt/Grid {:item true :xs 10}
        ($ mt/Slider
           {:style (clj->js {:color "#666"
                             :width "200px"})
            :value zoom
            :onChange (fn [e value]
                        (let [position (c.camera-controls/get-camera-position (:spaceship-camera-control @dom-atom))
                              direction (c.camera-controls/get-camera-direction (:camera @dom-atom))]
                          (go (>! service-chan #:event {:action :spaceship-camera-control/change-zoom
                                                        :detail {:spaceship-camera-control tool
                                                                 :position (vec position)
                                                                 :direction (vec direction)
                                                                 :zoom value}}))))
            :step 0.2 :min 0.4 :max 4 :marks true
            :getAriaValueText identity
            :aria-labelledby "discrete-slider-restrict"
            :valueLabelDisplay "auto"})]
       
      ;;  
       ]]]))


