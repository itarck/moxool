(ns film2.modules.recorder.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   [reagent.core :as r]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]
   [film2.modules.iovideo.m :as iovideo.m]
   [film2.modules.recorder.m :as recorder.m]))



(defmulti RecorderMenuView
  (fn [{:keys [recorder]} env]
    (:recorder/current-menu recorder)))


(defmethod RecorderMenuView :create-iovideo
  [{:keys [recorder]} {:keys [service-chan]}]
  (let [new-name (or (:recorder/iovideo-temp-name recorder) "")]
    [:> mt/Box {:component "form"}
     [:> mt/FormControl {:variant "standard"}
      [:> mt/InputLabel {:htmlFor "iovideo-name"} "iovideo文件名"]
      [:> mt/Input {:id "iovideo-name"
                    :value new-name
                    :on-change (fn [e]
                                 (let [v (j/get-in e [:target :value])
                                       event #:event {:action :recorder/change-iovideo-temp-name
                                                      :detail {:recorder recorder
                                                               :temp-name v}}]
                                   (go (>! service-chan event))))}]]
     [:> mt/FormControl
      [:> mt/Button {:variant "outlined"
                     :on-click (fn [e]
                                 (let [event #:event{:action :recorder/create-iovideo
                                                     :detail {:recorder recorder
                                                              :iovideo-name new-name}}]
                                   (go (>! service-chan event))))}
       "新建"]]]))


(defmethod RecorderMenuView :copy-ioframe
  [{:keys [recorder]} {:keys [conn service-chan]}]
  [:div "copy-ioframe"])


(defn RecorderToolView [{:keys [recorder]} {:keys [conn service-chan] :as env}]
  (let [recorder-1 @(p/pull conn '[*] (:db/id recorder))
        current-iovideo-id (get-in recorder-1 [:recorder/current-iovideo :db/id])
        iovideo-id-names @(p/q iovideo.m/all-id-and-names-query conn)]
    [:<>
     [:> mt/Grid {:container true :spacing 0}
      [:> mt/Grid {:item true :xs 2}
       [:div "2.选择文件"]
       [:> mt/Select {:value current-iovideo-id
                      :onChange (fn [e]
                                  (let [new-value (j/get-in e [:target :value])]
                                    (go (>! service-chan
                                            #:event {:action :recorder/change-current-iovideo
                                                     :detail {:recorder recorder-1
                                                              :iovideo {:db/id new-value}}}))))}
        (for [[id name] iovideo-id-names]
          ^{:key id}
          [:> mt/MenuItem {:value id} name])]]

      [:> mt/Grid {:item true :xs 2}
       [:div "3.动作"]
       [:> mt/Select {:value (:recorder/current-menu recorder-1)
                      :onChange (fn [e]
                                  (let [new-value (j/get-in e [:target :value])]
                                    (go (>! service-chan
                                            #:event {:action :recorder/change-menu
                                                     :detail {:recorder recorder-1
                                                              :menu-ident (keyword new-value)}}))))}
        (for [[id name] recorder.m/menu-ident-and-names]
          ^{:key id}
          [:> mt/MenuItem {:value id} name])]]
      
      [:> mt/Grid {:item true :xs 8}
       [RecorderMenuView {:recorder recorder-1} env]]]]))


(defn RecorderSceneView [{:keys [recorder]} {:keys [conn instance-atom] :as env}]
  (let [recorder-1 @(p/pull conn '[*] (:db/id recorder))
        current-iovideo-id (get-in recorder-1 [:recorder/current-iovideo :db/id])
        view-instance (get-in @instance-atom [:iovideo current-iovideo-id :ioframe-system/view])]
    (if view-instance
      view-instance
      [:div "default iovideo scene: " current-iovideo-id])))