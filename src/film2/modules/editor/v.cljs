(ns film2.modules.editor.v
  (:require
   [cljs.core.async :refer [go >!]]
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
        status (:editor/status editor-1)]
    [:<>
     [:div {:style {:position :absolute
                    :top "0px"
                    :width "100%"
                    :height "80px"}}
      [:p (str editor-1)]
      [:input {:type :button
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
        :ready (get-in @instance-atom [:scene-system :ioframe-system/view]))]]))