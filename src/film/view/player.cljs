(ns film.view.player
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :as async :refer [go >!]]
   ["react-audio-player" :default ReactAudioPlayer]
   [reagent-material-ui.core.slider :refer [slider]]
   [film.model.player :as m.player]
   [film.model.scene :as m.scene]
   [film.model.editor :as m.editor]))



;; view 

(defn AudioPlayerPanel [props out-chan system-conn]
  (let [player (m.player/sub-whole-player system-conn (get-in props [:player :db/id]))
        video (:player/current-video player)]
    [:div {:style {:position :absolute
                   :width "600px"
                   :top "5px"}}
     [:> ReactAudioPlayer
      {:src (:video/mp3 video)
       :controls true
       :preload "auto"
       :style {:width "400px"
               :height "40px"
               :opacity "0.7"}
       :onPause (fn [e]
                  (println "on pause")
                  (go (>! out-chan [:player :player/pause-play {:player player}])))
       :onPlay (fn [e]
                 (println "on play")
                 (go (>! out-chan [:player :player/start-play {:player player}])))
       :onSeeked (fn [e]
                   (let [current-time (j/get-in e [:target :currentTime])
                         seek-time (* 1000 current-time)]
                     (js/console.log "on Seeked:" seek-time)
                     (go (>! out-chan [:player :player/seek-play {:player player
                                                                  :seek-time seek-time}]))))
       :onVolumeChanged #(println "onVolumeChanged")}]]))


(defn PlayerPanel [props out-chan system-conn]
  (let [{:keys [editor scene]} props
        player (m.player/sub-player system-conn (get-in props [:player :db/id]))
        current-time (get-in player [:player/session :current-time])
        total-time (get-in player [:player/session :total-time])]
;;     (println "player view " player)
    [:div {:class "methodology-player"}
     [:div {:class "btn-toolbar"}

      [:h4 {:on-click #(go (>! out-chan [:player :player/start-play {:player player}]))}
       [:i {:class "bi bi-play"}]]
      [:h4 {:on-click #(go (>! out-chan [:player :player/pause-play {:player player}]))}
       [:i {:class "bi bi-pause"}]]

      [:h4 {:on-click #(go (>! out-chan [:editor :editor/start-record {:editor editor
                                                                       :scene scene}]))}
       [:i {:class "bi bi-record"}]]
      [:h4 {:on-click #(go (>! out-chan [:editor :editor/stop-record {:editor editor
                                                                      :scene scene}]))
            :style {:margin-right "15px"}}
       [:i {:class "bi bi-stop"}]]

      [:div {:class "input-group col"
             :style {:padding "0 10px"}}
       [slider {:value current-time
                :min 0
                :max total-time
                :onChange (fn [e value] (go (>! out-chan [:player :player/seek-play  {:player player
                                                                                      :seek-time value}])))}]]
      [:span.m-1 (/ total-time 1000.0)]]]))


(defn RootView [props env]
  (let [{:keys [studio-chan studio-conn]} env
        scene-view (get-in env [:scene-system :system/view])
        {:keys [player-id]} props
        player {:db/id player-id}]
    [:div {:style {:position :absolute
                   :height "720px"
                   :width "1280px"}}
     (when scene-view scene-view)
     #_[PlayerPanel {:player player} studio-chan studio-conn]
     [AudioPlayerPanel {:player player} studio-chan studio-conn]]))
