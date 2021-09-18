(ns film2.modules.studio.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   ["@material-ui/core" :as mt]
   [film2.modules.ioframe.m :as ioframe.m]
   [film2.modules.editor.v :as editor.v]
   [posh.reagent :as p]))




(defn StudioView [{:keys [studio] :as props} {:keys [conn instance-atom] :as env}]
  (let [studio-1 @(p/pull conn '[*] (:db/id studio))
        editor-1 @(p/pull conn '[*] (get-in studio-1 [:studio/editor :db/id]))
        current-ioframe-id (get-in editor-1 [:editor/current-ioframe :db/id])]
    [:<>
     [:div {:style {:position :absolute
                    :top "0px"
                    :width "100%"
                    :height "80px"}}
      [:> mt/Grid {:container true :spacing 0}
       [:> mt/Grid {:item true :xs 2}
        [:div "模式"]]
       [:> mt/Grid {:item true :xs 10}
        (case (:studio/mode studio-1)
          :editor [editor.v/EditorView {:editor (:studio/editor studio-1)} env])]]]
     [:div {:style {:position :absolute
                    :top "80px"
                    :height "720px"
                    :width "1280px"}}
      (case (:studio/mode studio-1)
        :editor (let [view-instance (get-in @instance-atom [:ioframe current-ioframe-id :ioframe-system/view])]
                  (if view-instance
                    view-instance
                    [:p "init"])))]]))