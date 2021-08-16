(ns film2.modules.editor.v
  (:require
   [posh.reagent :as p]))


(defn EditorView [{:keys [editor]} {:keys [conn instance-atom] :as env}]
  (let [editor-1 @(p/pull conn '[*] (:db/id editor))
        status (:editor/status editor-1)]
    [:div {:style {:position :absolute
                   :height "720px"
                   :width "1280px"}}
     (case status
       :init [:p "init"]
       :ready (get-in @instance-atom [:scene-system :system/view]))]))