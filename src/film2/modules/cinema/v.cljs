(ns film2.modules.cinema.v
  (:require
   [posh.reagent :as p]
   [film2.modules.editor.v :as editor.v]))


(defn UserMenuView []
  [:div {:style {:position :absolute
                 :top "5px"
                 :right "5px"}}
   "UserMenu"])


(defn CinemaView
  [{:keys [cinema] :as props} {:keys [conn service-chan] :as env}]
  (let [cinema-1 @(p/pull conn '[*] (:db/id cinema))]
    [:<>
     [:div {:style {:top "0"
                    :height "100%"
                    :width "100%"
                    :z-index 1}}
      [editor.v/EditorSceneView {:editor (:cinema/editor cinema-1)} env]]

     [UserMenuView]]))


