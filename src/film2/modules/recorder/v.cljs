(ns film2.modules.recorder.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   ["@material-ui/core" :as mt]
   [film2.modules.iovideo.m :as iovideo.m]
   [film2.modules.recorder.m :as recorder.m]
   [posh.reagent :as p]))


(defmulti RecorderMenuView
  (fn [{:keys [recorder]} env]
    (:recorder/current-menu recorder)))


(defmethod RecorderMenuView :create-iovideo
  [{:keys [recorder]} {:keys [conn service-chan]}]
  [:<>
   [:input {:type :button
            :value "创建iovideo"
            :on-click #(go (>! service-chan #:event{:action :recorder/create-iovideo
                                                    :detail {:recorder recorder
                                                             :iovideo-name (str "new-name-" (rand))}}))}]])

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