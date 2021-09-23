(ns film2.modules.recorder.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   ["@material-ui/core" :as mt]
   [film2.modules.iovideo.m :as iovideo.m]
   [film2.modules.recorder.m :as recorder.m]
   [posh.reagent :as p]))


(defn RecorderToolView [{:keys [recorder]} {:keys [conn service-chan] :as env}]
  (let [recorder-1 @(p/pull conn '[*] (:db/id recorder))
        current-iovideo-id (get-in recorder-1 [:recorder/current-iovideo :db/id])
        id-names @(p/q iovideo.m/all-id-and-names-query conn)
        current-time (get-in recorder-1 [:recorder/session :current-time])]
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
        (for [[id name] id-names]
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
                                                              :menu-ident new-value}}))))}
        (for [[id name] recorder.m/menu-ident-and-names]
          ^{:key id}
          [:> mt/MenuItem {:value id} name])]]
      
      [:> mt/Grid {:item true :xs 8}

       [:input {:type :button
                :value "load"
                :on-click #(go (>! service-chan #:event{:action :recorder/load-current-iovideo
                                                        :detail {:recorder recorder-1}}))}]
       [:input {:type :button
                :value "pause"
                :on-click #(go (>! service-chan #:event{:action :recorder/pause-play
                                                        :detail {:recorder recorder-1}}))}]
       [:input {:type :button
                :value "play"
                :on-click #(go (>! service-chan #:event{:action :recorder/start-play
                                                        :detail {:recorder recorder-1}}))}]
       [:p (str "当前时间： " current-time)]]]]))


(defn RecorderSceneView [{:keys [recorder]} {:keys [conn instance-atom] :as env}]
  (let [recorder-1 @(p/pull conn '[*] (:db/id recorder))
        current-iovideo-id (get-in recorder-1 [:recorder/current-iovideo :db/id])
        view-instance (get-in @instance-atom [:iovideo current-iovideo-id :ioframe-system/view])]
    (if view-instance
      view-instance
      [:div "default iovideo scene: " current-iovideo-id])))