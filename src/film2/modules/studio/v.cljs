(ns film2.modules.studio.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   ["@material-ui/core" :as mt]
   [film2.modules.ioframe.m :as ioframe.m]
   [film2.modules.editor.v :as editor.v]
   [film2.modules.player.v :as player.v]
   [posh.reagent :as p]))




(defn StudioView [{:keys [studio] :as props} {:keys [conn service-chan] :as env}]
  (let [studio-1 @(p/pull conn '[*] (:db/id studio))
        modes [:editor :player :recorder]]
    [:<>
     [:div {:style {:position :absolute
                    :top "0px"
                    :width "100%"
                    :height "80px"
                    :padding "5px"}}
      [:> mt/Grid {:container true :spacing 0}
       [:> mt/Grid {:item true :xs 2}
        [:div "1.工作模式： " ]
        [:div 
         [:> mt/Select {:value (:studio/mode studio-1)
                        :onChange (fn [e]
                                    (let [new-value (j/get-in e [:target :value])
                                          event #:event {:action :studio/change-mode
                                                         :detail {:studio studio-1
                                                                  :new-mode (keyword new-value)}}]
                                      (go (>! service-chan event))))}
          (for [mode modes]
            ^{:key mode}
            [:> mt/MenuItem {:value mode} mode])]]]
       
       [:> mt/Grid {:item true :xs 10}
        (case (:studio/mode studio-1)
          :editor [editor.v/EditorToolView {:editor (:studio/editor studio-1)} env]
          :player [player.v/PlayerToolView {:player (:studio/player studio-1)} env]
          :recorder [:div "recorder"])]]]
     
     
     [:div {:style {:position :absolute
                    :top "80px"
                    :height "720px"
                    :width "1280px"}}
      (case (:studio/mode studio-1)
        :editor [editor.v/EditorSceneView {:editor (:studio/editor studio-1)} env]
        :player [player.v/PlayerSceneView {:player (:studio/player studio-1)} env]
        :recorder [:div "recorder scene"])]]))


