(ns film2.modules.cinema.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]
   [film2.modules.editor.v :as editor.v]))


(defn UserMenuView [{:keys [cinema]} {:keys [service-chan]}]
  [:div {:style {:position :absolute
                 :top "5px"
                 :right "5px"}}
   [:> mt/Select {:value "a"
                  :style {:color "white"
                          :background "rgba(255, 255, 255, 0.2)"
                          :padding "0 10px"}
                  :onChange (fn [e]
                              (let [new-value (j/get-in e [:target :value])
                                    event #:event {:action :cinema/change-ioframe
                                                   :detail {:ioframe-name (keyword new-value)}}]
                                (println event)
                                #_(go (>! service-chan event))))}

    (for [ioframe-name (:cinema/ioframe-names cinema)]
      ^{:key ioframe-name}
      [:> mt/MenuItem {:value ioframe-name} ioframe-name])]])


(defn CinemaView
  [{:keys [cinema] :as props} {:keys [conn service-chan] :as env}]
  (let [cinema-1 @(p/pull conn '[*] (:db/id cinema))]
    [:<>
     [:div {:style {:top "0"
                    :height "100%"
                    :width "100%"
                    :z-index 1}}
      [editor.v/EditorSceneView {:editor (:cinema/editor cinema-1)} env]]

     [UserMenuView {:cinema cinema-1} env]]))


