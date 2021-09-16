(ns film2.modules.editor.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   ["@material-ui/core" :as mt]
   [film2.modules.ioframe.m :as ioframe.m]
   [posh.reagent :as p]))


(defn FreeView [{:keys [editor]} {:keys [conn instance-atom service-chan] :as env}]
  (let [editor-1 @(p/pull conn '[*] (:db/id editor))
        status (:editor/status editor-1)]
    [:div {:style {:position :absolute
                   :height "100%"
                   :width "100%"}}
     (case status
       :init [:p "init"]
       :ready (get-in @instance-atom [:scene-system :ioframe-system/view]))]))


(defn EditorView [{:keys [editor]} {:keys [conn instance-atom service-chan] :as env}]
  (let [editor-1 @(p/pull conn '[*] (:db/id editor))
        current-ioframe-id (get-in editor-1 [:editor/current-frame :db/id])
        status (:editor/status editor-1)
        ioframes @(p/q ioframe.m/all-id-and-names-query conn)]
    [:<>
     [:div {:style {:position :absolute
                    :top "0px"
                    :width "100%"
                    :height "80px"}}
      [:p (str editor-1)]

      [:> mt/Select {:value current-ioframe-id
                     :onChange (fn [e]
                                 (let [new-value (j/get-in e [:target :value])]
                                   (go (>! service-chan
                                           #:event {:action :editor/change-current-ioframe
                                                    :detail {:editor editor-1
                                                             :ioframe {:db/id new-value} }}))))}
       (for [[id name] ioframes]
         ^{:key id}
         [:> mt/MenuItem {:value id} name])]
      
      #_[:input {:type :button
               :value "pull"
               :on-click #(go (>! service-chan #:event{:action :editor/pull-current-frame}))}]
      [:input {:type :button
               :value "load"
               :on-click #(go (>! service-chan #:event{:action :editor/load-current-frame}))}]]
     [:div {:style {:position :absolute
                    :top "80px"
                    :height "720px"
                    :width "1280px"}}
      (case status
        :init [:p "init"]
        :ready (get-in @instance-atom [:ioframe current-ioframe-id :ioframe-system/view]))]]))