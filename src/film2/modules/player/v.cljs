(ns film2.modules.player.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   ["@material-ui/core" :as mt]
   [film2.modules.iovideo.m :as iovideo.m]
   [posh.reagent :as p]))


(defn PlayerView [{:keys [player]} {:keys [conn service-chan] :as env}]
  (let [player-1 @(p/pull conn '[*] (:db/id player))
        current-iovideo-id (get-in player-1 [:player/current-iovideo :db/id])
        id-names @(p/q iovideo.m/all-id-and-names-query conn)]
    [:<>
     [:> mt/Select {:value current-iovideo-id
                    :onChange (fn [e]
                                (let [new-value (j/get-in e [:target :value])]
                                  (go (>! service-chan
                                          #:event {:action :player/change-current-iovideo
                                                   :detail {:player player-1
                                                            :iovideo {:db/id new-value}}}))))}
      (for [[id name] id-names]
        ^{:key id}
        [:> mt/MenuItem {:value id} name])]

     [:input {:type :button
              :value "play"
              :on-click #(go (>! service-chan #:event{:action :player/play-current-iovideo
                                                      :detail {:player player-1}}))}]]))
