(ns film.view.editor
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :as async :refer [go >! <! chan alts! timeout put! take! go-loop]]
   [film.model.scene :as m.scene]
   [film.model.editor :as m.editor]
   [film.model.player :as m.player]
   [film.view.player :as v.player]))

;; view 

(defn Button [name on-click]
  [:input {:type :button
           :value name
           :onClick on-click}])


(defn ActionPanel [props out-chan]
  (let [{:keys [editor scene system-name]} props]
    [:div
     [:p "Action Panel"]
     [Button "start record" #(go (>! out-chan [:editor :editor/start-record {:editor editor
                                                                             :scene scene}]))]
     [Button "stop record" #(go (>! out-chan [:editor :editor/stop-record {:editor editor
                                                                           :scene scene}]))]
     [Button "download db" #(go (>! out-chan [:editor :editor/download-db]))]

     [Button "save db" #(go (>! out-chan [:editor :editor/save-db {:db-name system-name}]))]
     [Button "load db" #(go (>! out-chan [:editor :editor/load-db {:db-name system-name}]))]]))


(defn StatusPanel [props out-chan system-conn]
  (let [{:keys [editor player]} props
        player (m.player/sub-player system-conn (:db/id player))
        scene-id (get-in editor [:editor/scene :db/id])
        scene (m.scene/sub-whole-scene system-conn scene-id)
        ;; videos (:video/_scene scene)
        ]
    [:div
     [:p "Status Panel"]
     [:p (str player)]
     #_[:p (str (last videos))]]))


(defn VideoComponent [props out-chan]
  (let [local-ref (atom nil)]
    (fn [props out-chan]
      (let [{:keys [video editor player]} props
            {:video/keys [start-timestamp mp3]} video]
        [:div.card
         [:div.card-header (:db/id video) ": " (:video/name video)]
         [:ul.list-group.list-group-flush
          [:li.list-group-item "total-time: " (:video/total-time video)]
          [:li.list-group-item "mp3: " (if mp3 mp3 "还没上传")
           [:input {:type :file
                    :ref (fn [el] (reset! local-ref el))
                    :id (str "file-" (:db/id video))
                    :onChange (fn [e]
                                (let [el @local-ref
                                      file (first (j/get el :files))]
                                  (go (>! out-chan [:video :video/upload-mp3 {:video video
                                                                              :filename (str start-timestamp ".mp3")
                                                                              :file file}]))))}]]
          [:li.list-group-item "操作: "
           [Button "加载视频" #(go (>! out-chan
                                   [:player :player/open-video {:player-id (:db/id player)
                                                               :video-id (:db/id video)}]))]]]]))))


(defn VideoPanel [props out-chan]
  (let [{:keys [editor player videos]} props]
    [:div
     [:p "Video Panel"]
     (for [video (reverse videos)]
       ^{:key (:db/id video)}
       [VideoComponent {:video video
                        :player player
                        :editor editor}
        out-chan])]))


(defn EditorView [props out-chan system-conn scene-view]
  (let [{:keys [system-name editor-id player-id]}  props
        editor (m.editor/sub-editor system-conn editor-id)
        player {:db/id player-id}
        scene (m.scene/sub-whole-scene system-conn (get-in editor [:editor/scene :db/id]))
        videos (:video/_scene scene)]
    [:div {:style {:position :absolute
                   :height "720px"
                   :width "1280px"}}
     (when scene-view
       scene-view)

     [v.player/PlayerPanel {:player player} out-chan system-conn]
     #_[v.player/AudioPlayerPanel {:player player} out-chan system-conn]

     [:div.container-fluid.m-0.p-0
      [:div.row.gx-0
       [:div.col.m-2.p-2 {:style {:background "#ddd"}}
        [ActionPanel {:system-name system-name :editor editor :scene scene} out-chan]]
       [:div.col.m-2.p-2 {:style {:background "#ddd"}}
        [VideoPanel {:editor editor :player player :videos videos} out-chan]]
       [:div.col.m-2.p-2 {:style {:background "#ddd"}}
        [StatusPanel {:editor editor} out-chan system-conn]]]]]))


(defn StudioView [props env]
  (let [{:keys [system-name]} props
        {:keys [studio-chan studio-conn]} env
        scene-view (get-in env [:scene-system :system/view])]
    [EditorView {:system-name system-name
                 :editor-id [:editor/name "default"]
                 :player-id [:player/name "default"]}
     studio-chan studio-conn scene-view]))
