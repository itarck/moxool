(ns film2.modules.editor.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   ["@material-ui/core" :as mt]
   [film2.modules.ioframe.m :as ioframe.m]
   [posh.reagent :as p]))



(defn IOFrameToolView [{:keys [editor]} {:keys [conn service-chan] :as env}]
  (let [editor-1 @(p/pull conn '[*] (:db/id editor))
        current-ioframe-id (get-in editor-1 [:editor/current-frame :db/id])
        ioframes @(p/q ioframe.m/all-id-and-names-query conn)]
    [:<>
     [:> mt/Select {:value current-ioframe-id
                    :onChange (fn [e]
                                (let [new-value (j/get-in e [:target :value])]
                                  (go (>! service-chan
                                          #:event {:action :editor/change-current-ioframe
                                                   :detail {:editor editor-1
                                                            :ioframe {:db/id new-value}}}))))}
      (for [[id name] ioframes]
        ^{:key id}
        [:> mt/MenuItem {:value id} name])]

     [:input {:type :button
              :value "load"
              :on-click #(go (>! service-chan #:event{:action :editor/load-current-frame}))}]]))


(defn EditorView [{:keys [editor] :as props} {:keys [conn instance-atom] :as env}]
  (let [editor-1 @(p/pull conn '[*] (:db/id editor))
        current-ioframe-id (get-in editor-1 [:editor/current-frame :db/id])]
    [:<>
     [:div {:style {:position :absolute
                    :top "0px"
                    :width "100%"
                    :height "80px"}}
      [:> mt/Grid {:container true :spacing 0}
       [:> mt/Grid {:item true :xs 2}
        [:div "模式"]
        ]
       [:> mt/Grid {:item true :xs 10}
        [IOFrameToolView props env]]]]
     [:div {:style {:position :absolute
                    :top "80px"
                    :height "720px"
                    :width "1280px"}}
      (case (:editor/status editor-1)
        :init [:p "init"]
        :ready (get-in @instance-atom [:ioframe current-ioframe-id :ioframe-system/view]))]]))