(ns astronomy.view.user.astronomical-point-tool
  (:require
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [helix.core :refer [$ defnc]]
   [posh.reagent :as p]
   [goog.string :as gstring]
   ["@material-ui/core" :as mt]
   [shu.three.vector3 :as v3]
   [astronomy.component.tool :as c.tool]
   [astronomy.component.mouse :as c.mouse]
   [astronomy.model.astronomical-point :as m.apt]
   [astronomy.model.coordinate :as m.coordinate]))

;; data

(def apt-tool-1
  #:astronomical-point-tool
   {:tool/name "astronomical-point-tool"
    :tool/chinese-name "标注点工具"
    :tool/icon "/image/moxool/crosshair-tool.jpg"
    :tool/type :astronomical-point-tool
    :tool/panels [:create-panel :query-panel :pull-panel :delete-panel]
    :tool/current-panel :create-panel
    :entity/type :astronomical-point-tool})

;; views


(defn AstronomicalPointCreatePanelView [props env]
  (let [{:keys [tool]} props]
    [:div.p-2
     ($ mt/Grid {:container true :spacing 1}
        ($ mt/Grid {:item true :xs 12}
           ($ mt/Typography {:variant "subtitle1"} "点击任意位置新建")))]))


(defn AstronomicalPointDeletePanelView [props env]
  (let [{:keys [tool]} props]
    [:div.p-2
     ($ mt/Grid {:container true :spacing 1}
        ($ mt/Grid {:item true :xs 12}
           ($ mt/Typography {:variant "subtitle1"} "点击天球坐标点删除")))]))

(defn AstronomicalPointPullPanelView
  [props {:keys [conn service-chan]}]
  (let [{:keys [tool]} props
        pull-id (:astronomical-point-tool/pull-id tool)
        point @(p/pull conn '[*] pull-id)]
    [:div.p-2
     
     (if point
       (let [{:astronomical-point/keys [longitude latitude size]} point]
         ($ mt/Grid {:container true :spacing 1}
            ($ mt/Grid {:item true :xs 12}
               ($ mt/Typography {:variant "subtitle1"} "当前选中点"))

            ($ mt/Grid {:item true :xs 5}
               ($ mt/Typography {:variant "subtitle2"} "id"))
            ($ mt/Grid {:item true :xs 7}
               ($ mt/Typography {:variant "subtitle2"}
                  (:db/id point)))


            ($ mt/Grid {:item true :xs 5}
               ($ mt/Typography {:variant "subtitle2"} "经纬度"))
            ($ mt/Grid {:item true :xs 7}
               ($ mt/Typography {:variant "subtitle2"}
                  (str "[" (gstring/format "%0.2f" longitude) ", " (gstring/format "%0.2f" latitude) "]")))

            ($ mt/Grid {:item true :xs 5}
               ($ mt/Typography {:variant "subtitle2"} "尺寸"))

            ($ mt/Grid {:item true :xs 7}
               ($ mt/Slider
                  {:style (clj->js {:color "#666"})
                   :value size
                   :onChange (fn [e value]
                               (go (>! service-chan #:event {:action :astronomical-point-tool/change-size
                                                             :detail {:astronomical-point point
                                                                      :size value}})))
                   :step 0.01 :min 0.1 :max 2 :marks true
                   :getAriaValueText identity
                   :aria-labelledby "discrete-slider-restrict"
                   :valueLabelDisplay "auto"}))))
       
       ($ mt/Grid {:item true :xs 12}
          ($ mt/Typography {:variant "subtitle1"} "点击天球坐标点读取")))]))


(defn AstronomicalPointToolView [props {:keys [service-chan conn] :as env}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        {:tool/keys [panels current-panel]} tool
        panel-props {:panels (vec (for [panel panels]
                                    {:name panel
                                     :onClick #(go (>! service-chan #:event {:action :tool/change-panel
                                                                             :detail {:tool tool
                                                                                      :current-panel panel}}))}))
                     :current-panel current-panel}]
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
         [:> c.tool/PanelsComponent panel-props]]]

       [:> mt/Grid {:container true :spacing 1}
        (case current-panel
          :create-panel [AstronomicalPointCreatePanelView {:tool tool} env]
          :delete-panel [AstronomicalPointDeletePanelView {:tool tool} env]
          :pull-panel [AstronomicalPointPullPanelView {:tool tool} env]
          nil)]

    ;;    
       ]]]))


(defn is-mouse-in-tool-panel? [mouse]
  (let [{:mouse/keys [page-x page-y]} mouse]
    (and (> page-x 1000)
         (> page-y 600))))

(defn is-mouse-in-backpack? [mouse]
  (let [{:mouse/keys [page-y]} mouse]
    (> page-y 750)))



(defn AstronomicalPointHudView [{:keys [user astro-scene] :as props} {:keys [conn dom-atom]}]
  (let [tool @(p/pull conn '[:db/id :tool/current-panel] (get-in props [:tool :db/id]))]
    (when (= (:tool/current-panel tool) :create-panel)
      (let [mouse @(p/pull conn '[*] (get-in user [:person/mouse :db/id]))
            {:mouse/keys [page-x page-y]} mouse]
        (when-not (or (is-mouse-in-backpack? mouse)
                      (is-mouse-in-tool-panel? mouse))
          (let [mouse-direction (c.mouse/get-mouse-direction-vector3 (:three-instance @dom-atom))
                camera-position (c.mouse/get-camera-position (:three-instance @dom-atom))
                act-1 @(p/pull conn '[*] [:coordinate/name "赤道天球坐标系"])
                local-vector3 (v3/add (v3/multiply-scalar mouse-direction (:astronomical-coordinate/radius act-1))
                                    camera-position)
                scene-coordinate (m.coordinate/sub-scene-coordinate conn astro-scene)
                system-vector (m.coordinate/to-system-vector scene-coordinate local-vector3)
                apt-1 (m.apt/from-position system-vector)
                [longitude latitude] (m.apt/get-longitude-and-latitude apt-1)]
            [:div.p-2 {:style {:position :absolute
                               :top (+ page-y 5)
                               :left (+ page-x 5)
                               :color "white"
                               :background "rgba(255, 255, 255, 0.3)"}}
             (str "[" (gstring/format "%.2f" longitude)
                  ", " (gstring/format "%.2f" latitude) "]")]))))))
