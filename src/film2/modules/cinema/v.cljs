(ns film2.modules.cinema.v
  (:require
   ["react-three-fiber" :refer [Canvas]]
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]
   [helix.core :refer [defnc $]]
   ["@react-three/drei" :refer [Stars OrbitControls]]
   [film2.modules.editor.v :as editor.v]))


(defn UserMenuView [{:keys [cinema]} {:keys [service-chan]}]
  [:div {:style {:position :absolute
                 :top "5px"
                 :right "5px"}}
   [:> mt/Select {:value (:cinema/current-ioframe-name cinema)
                  :style {:color "white"
                          :background "rgba(255, 255, 255, 0.2)"
                          :padding "0 10px"}
                  :onChange (fn [e]
                              (let [new-value (j/get-in e [:target :value])
                                    event #:event {:action :cinema/change-ioframe
                                                   :detail {:cinema cinema
                                                            :ioframe-name new-value}}]
                                (go (>! service-chan event))))}
    (for [ioframe-name (:cinema/ioframe-names cinema)]
      ^{:key ioframe-name}
      [:> mt/MenuItem {:value ioframe-name} ioframe-name])]])


(defn CinemaScreenView
  [{:keys [cinema] :as props} {:keys [conn] :as env}]
  (let [cinema-1 @(p/pull conn '[*] (:db/id cinema))]
    [:<>
     [:div {:style {:top "0"
                    :height "100%"
                    :width "100%"
                    :z-index 1}}
      [editor.v/EditorSceneView {:editor (:cinema/editor cinema-1)} env]]
     [UserMenuView {:cinema cinema-1} env]]))


(defn CinemaEntranceView
  [props env]
  [:> Canvas {:style {:background :black
                      :style {:height "100%"
                              :width "100%"}}
              :shadowMap true}
   ($ Stars {:radius 100 :depth 100 :count 3000 :factor 4 :saturation 0 :fade true})
   ($ OrbitControls)])


(defn CinemaView
  [{:keys [cinema] :as props} {:keys [conn] :as env}]
  (let [cinema-1 @(p/pull conn '[*] (:db/id cinema))]
    (if (:cinema/login? cinema-1)
      [CinemaScreenView {:cinema cinema-1} env]
      [CinemaEntranceView {:cinema cinema-1} env])))


