(ns film2.modules.studio.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]
   [film2.modules.editor.v :as editor.v]
   [film2.modules.player.v :as player.v]
   [film2.modules.recorder.v :as recorder.v]))


(defn StudioView
  [{:keys [studio] :as props} {:keys [conn service-chan] :as env}]
  (let [studio-1 @(p/pull conn '[*] (:db/id studio))
        modes [:editor :player :recorder]]
    [:<>
     [:div {:style {:position :absolute
                    :top "0px"
                    :margin "5px"
                    :width "60%"
                    :background-color "rgba(255,255,255,0.2)"
                    :z-index 10}}
      [:div {:style {:margin "5px"
                     :height "40px"
                     :padding "5px 10px"
                     :background-color "rgba(255,255,255,0.5)"}}
       [:> mt/Grid {:container true :spacing 0}
        [:> mt/Grid {:item true :xs 4}
         [:span "1.模式："]
         [:span
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

        [:> mt/Grid {:item true :xs 8}
         (case (:studio/mode studio-1)
           :editor [editor.v/EditorToolView {:editor (:studio/editor studio-1)} env]
           :player [player.v/PlayerToolView {:player (:studio/player studio-1)} env]
           :recorder [recorder.v/RecorderToolView {:recorder (:studio/recorder studio-1)} env])]]]]

     [:div {:style {:top "0"
                    :height "100%"
                    :width "100%"
                    :z-index 1}}
      (case (:studio/mode studio-1)
        :editor [editor.v/EditorSceneView {:editor (:studio/editor studio-1)} env]
        :player [player.v/PlayerSceneView {:player (:studio/player studio-1)} env]
        :recorder [recorder.v/RecorderSceneView {:recorder (:studio/recorder studio-1)} env])]]))


