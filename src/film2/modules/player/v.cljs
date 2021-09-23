(ns film2.modules.player.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   ["@material-ui/core" :as mt]
   [film2.modules.iovideo.m :as iovideo.m]
   [posh.reagent :as p]))


(defn PlayerToolView [{:keys [player]} {:keys [conn service-chan] :as env}]
  (let [player-1 @(p/pull conn '[*] (:db/id player))
        current-iovideo-id (get-in player-1 [:player/current-iovideo :db/id])
        id-names @(p/q iovideo.m/all-id-and-names-query conn)
        current-time (get-in player-1 [:player/session :current-time])]
    [:<>
     [:> mt/Grid {:container true :spacing 0}
      [:> mt/Grid {:item true :xs 2}
       [:div "2.选择文件"]
       [:> mt/Select {:value current-iovideo-id
                      :onChange (fn [e]
                                  (let [new-value (j/get-in e [:target :value])]
                                    (go (>! service-chan
                                            #:event {:action :player/change-current-iovideo
                                                     :detail {:player player-1
                                                              :iovideo {:db/id new-value}}}))))}
        (for [[id name] id-names]
          ^{:key id}
          [:> mt/MenuItem {:value id} name])]]

      [:> mt/Grid {:item true :xs 1} [:span "3.动作"]]
      [:> mt/Grid {:item true :xs 9}

       [:input {:type :button
                :value "load"
                :on-click #(go (>! service-chan #:event{:action :player/load-current-iovideo
                                                        :detail {:player player-1}}))}]
       [:input {:type :button
                :value "pause"
                :on-click #(go (>! service-chan #:event{:action :player/pause-play
                                                        :detail {:player player-1}}))}]
       [:input {:type :button
                :value "play"
                :on-click #(go (>! service-chan #:event{:action :player/start-play
                                                        :detail {:player player-1}}))}]
       [:p (str "当前时间： " current-time)]]]
   

     
     
     ]))


(defn PlayerSceneView [{:keys [player]} {:keys [conn instance-atom] :as env}]
  (let [player-1 @(p/pull conn '[*] (:db/id player))
        current-iovideo-id (get-in player-1 [:player/current-iovideo :db/id])
        view-instance (get-in @instance-atom [:iovideo current-iovideo-id :ioframe-system/view])]
    (if view-instance
      view-instance
      [:div "default iovideo scene: " current-iovideo-id])))