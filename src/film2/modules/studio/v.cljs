(ns film2.modules.studio.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   ["@material-ui/core" :as mt]
   [film2.modules.ioframe.m :as ioframe.m]
   [film2.modules.editor.v :as editor.v]
   [posh.reagent :as p]))




(defn StudioView [{:keys [studio] :as props} {:keys [conn instance-atom service-chan] :as env}]
  (let [studio-1 @(p/pull conn '[*] (:db/id studio))
        editor-1 @(p/pull conn '[*] (get-in studio-1 [:studio/editor :db/id]))
        current-ioframe-id (get-in editor-1 [:editor/current-ioframe :db/id])
        modes [:editor :player :recorder]]
    [:<>
     [:div {:style {:position :absolute
                    :top "0px"
                    :width "100%"
                    :height "80px"}}
      [:> mt/Grid {:container true :spacing 0}
       [:> mt/Grid {:item true :xs 2}
        [:div "模式： " 
         [:> mt/Select {:value (:studio/mode studio-1)
                        :onChange (fn [e]
                                    (let [new-value (j/get-in e [:target :value])]
                                      (go (>! service-chan
                                              #:event {:action :studio/change-mode
                                                       :detail {:studio studio-1
                                                                :new-mode (keyword new-value)}}))))}
          (for [mode modes]
            ^{:key mode}
            [:> mt/MenuItem {:value mode} mode])]]]
       
       [:> mt/Grid {:item true :xs 10}
        (case (:studio/mode studio-1)
          :editor [editor.v/EditorView {:editor (:studio/editor studio-1)} env]
          :player [:div "player"]
          :recorder [:div "recorder"])]]]
     [:div {:style {:position :absolute
                    :top "80px"
                    :height "720px"
                    :width "1280px"}}
      (case (:studio/mode studio-1)
        :editor (let [view-instance (get-in @instance-atom [:ioframe current-ioframe-id :ioframe-system/view])]
                  (if view-instance
                    view-instance
                    [:p "editor default view"]))
        :player [:div "player scene"]
        :recorder [:div "recorder scene"])]]))


