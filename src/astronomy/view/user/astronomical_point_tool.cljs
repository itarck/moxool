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
    [:<>]))


(defn AstronomicalPointQueryPanelView [props env]
  (let [{:keys [tool]} props]
    [:div
     [:p "query-panel"]
     [:span (str tool)]]))

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
          :query-panel [AstronomicalPointQueryPanelView {:tool tool} env]
          [:div])]

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
                apt-1 (m.apt/astronomical-point system-vector)
                [longitude latitude] (m.apt/get-longitude-and-latitude apt-1)]
            [:div.p-2 {:style {:position :absolute
                               :top (+ page-y 5)
                               :left (+ page-x 5)
                               :color "white"
                               :background "rgba(255, 255, 255, 0.3)"}}
             (str "[" (gstring/format "%.2f" longitude)
                  ", " (gstring/format "%.2f" latitude) "]")]))))))