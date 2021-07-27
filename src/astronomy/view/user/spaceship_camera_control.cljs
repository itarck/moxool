(ns astronomy.view.user.spaceship-camera-control
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   [cljs-bean.core :refer [bean ->clj ->js]]
   [helix.core :refer [defnc $]]
   [posh.reagent :as p]
   [goog.string :as gstring]
   ["@material-ui/core" :as mt]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   [astronomy.model.astro-scene :as m.astro-scene]
   [shu.astronomy.light :as shu.light]
   [astronomy.component.camera-controls :as c.camera-controls]))



(def spaceship-camera-control-entity
  #:spaceship-camera-control
   {:name "default"
    :position [2000 2000 2000]
    :up [0 1 0]
    :target [0 0 0]
    :min-distance 10000
    :tool/name "spaceship camera tool"
    :tool/chinese-name "相机控制"
    :tool/icon "/image/pirate/cow.jpg"
    :entity/type :spaceship-camera-control})



(def max-distance (* 10000 46500000000 shu.light/light-year-unit))


(defn SpaceshipCameraControlView [{:keys [astro-scene] :as props} {:keys [conn dom-atom] :as env}]
  (let [camera-control @(p/pull conn '[*] (get-in props [:spaceship-camera-control :db/id]))
        center-cele-id @(p/q m.astro-scene/find-center-celestial-id-query conn (get-in props [:astro-scene :db/id]))
        center-celestial @(p/pull conn '[:celestial/radius :db/id] center-cele-id)
        {:spaceship-camera-control/keys [mode position up target zoom]} camera-control
        orbit-props {:up (->js up)
                     :target (->js target)
                     :position (->js position)
                     :minDistance (* 1.01 (* (:celestial/radius center-celestial) (:scene/scale astro-scene)))
                     :maxDistance max-distance
                     :zoom zoom}
        surface-props {:up (->js up)
                       :target (->js target)
                       :position (->js position)
                       :minDistance 1e-3
                       :maxDistance 1e-3
                       :zoom zoom}]
    ;; (println "!!camera control loaded: " )
    (if (= mode :orbit-control)
      ($ c.camera-controls/CameraControlsComponent
         {:azimuthRotateSpeed -0.3
          :polarRotateSpeed -0.3
          :domAtom dom-atom
          :& orbit-props})

      ($ c.camera-controls/CameraControlsComponent
         {:azimuthRotateSpeed -0.3
          :polarRotateSpeed -0.3
          :domAtom dom-atom
          :& surface-props}))))


;; tool view


(defn SpaceshipCameraToolView [props {:keys [service-chan conn dom-atom]}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        {:spaceship-camera-control/keys [surface-ratio zoom]} tool
        mode-and-names [[:surface-control "星球表面移动模式"]
                        [:orbit-control "轨道环绕运动模式"]
                        [:static-control "固定位置模式"]]]
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
         [:> mt/Select {:value (:spaceship-camera-control/mode tool)
                        :onChange (fn [e]
                                    (let [new-mode (j/get-in e [:target :value])]
                                      (go (>! service-chan
                                              #:event {:action :spaceship-camera-control/change-mode
                                                       :detail {:new-mode new-mode}}))))}
          (for [[mode mode-name] mode-and-names]
            ^{:key mode}
            [:> mt/MenuItem {:value mode} mode-name])]]

        (when (m.spaceship/surface-mode? tool)
            [:> mt/Grid {:item true :xs 12}
             [:> mt/Typography {:variant "subtitle1"} (str "高度/星球半径：" (gstring/format "%0.4f" (- surface-ratio 1)))]
             ($ mt/Slider
                {:style (->js {:color "#666"
                               :width "200px"})
                 :value surface-ratio
                 :onChange (fn [e value]
                             (go (>! service-chan #:event {:action :spaceship-camera-control/change-surface-ratio
                                                           :detail {:spaceship-camera-control-id (:db/id tool)
                                                                    :surface-ratio value}})))
                 :step 0.0002 :min 1.0000 :max 1.01 :marks true
                 :getAriaValueText identity
                 :aria-labelledby "discrete-slider-restrict"
                 :valueLabelDisplay "auto"})])]]]]))