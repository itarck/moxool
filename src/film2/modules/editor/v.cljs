(ns film2.modules.editor.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   ["@material-ui/core" :as mt]
   [film2.modules.ioframe.m :as ioframe.m]
   [posh.reagent :as p]))



(defn EditorView [{:keys [editor]} {:keys [conn service-chan] :as env}]
  (let [editor-1 @(p/pull conn '[*] (:db/id editor))
        current-ioframe-id (get-in editor-1 [:editor/current-ioframe :db/id])
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
              :on-click #(go (>! service-chan #:event{:action :editor/load-current-ioframe
                                                      :detail {:editor editor-1}}))}]]))
