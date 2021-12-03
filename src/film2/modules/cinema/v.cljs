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

(defn LoginView
  [{:keys [cinema]} {:keys [service-chan]}]
  (let [local-atom (atom {:email ""
                          :angel-code ""})]
    (fn [_props _env]
      [:div {:class "cinema-login"}
       [:div {:class "cinema-login-content"}
        [:div.p-2
         [:> mt/Box {:component "form"}
          [:> mt/Grid {:container true :spacing 1
                       :direction "column"
                       :alignItems "center"
                       :style {:padding "10px"}}
           [:> mt/Grid {:item true :xs 10}
            [:> mt/Typography {:variant "h6"}
             "请输入试用码"]]
           [:> mt/Grid {:item true :xs 10}
            [:> mt/TextField {:id "standard-basic" :label "邮箱" :variant "standard" :style {:width "300px"}
                              :onChange #(swap! local-atom assoc :email (j/get-in % [:target :value]))}]]
           [:> mt/Grid {:item true :xs 10}
            [:> mt/TextField {:id "standard-basic" :label "试用码" :variant "standard" :style {:width "300px"}
                              :onChange #(swap! local-atom assoc :angel-code (j/get-in % [:target :value]))}]]
           [:> mt/Grid {:item true :xs 5}
            [:> mt/Button {:variant "outlined"
                           :on-click (fn []
                                       (let [{:keys [email angel-code]} @local-atom]
                                         (go (>! service-chan #:event{:action :cinema/varify-angle-code
                                                                      :detail {:cinema cinema
                                                                               :email email
                                                                               :angel-code angel-code
                                                                               :from-user? true}}))))}
             "确定"]]]]]]])))

(defn BackgroundView []
  [:> Canvas {:style {:background :black
                      :style {:height "100%"
                              :width "100%"}}
              :shadowMap true}
   ($ Stars {:radius 100 :depth 100 :count 3000 :factor 4 :saturation 0 :fade true})
   ($ OrbitControls)])


(defn CinemaEntranceView
  [props env]
  [:<>
   [BackgroundView]
   [LoginView props env]])


(defn CinemaView
  [{:keys [cinema] :as props} {:keys [conn] :as env}]
  (let [cinema-1 @(p/pull conn '[*] (:db/id cinema))]
    (case (:cinema/login-state cinema-1)
      :fail [CinemaEntranceView {:cinema cinema-1} env]
      :success [CinemaScreenView {:cinema cinema-1} env]
      [BackgroundView])))


