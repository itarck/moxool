(ns astronomy.tools.ellipse-orbit-tool.h
  (:require
   [datascript.core :as d]
   [astronomy.service.effect :as s.effect :refer [effects]]))


;; handle-event version

(defmulti handle-event (fn [_props _env event] (:event/action event)))

(defmethod handle-event :ellipse-orbit-tool/log
  [_props _env {:event/keys [detail]}]
  (println ":ellipse-orbit-tool/log")
  #:effect {:action :log :detail detail})

(defmethod handle-event :ellipse-orbit-tool/change-show-helper-lines
  [_props _env {:event/keys [detail]}]
  (let [{:keys [ellipse-orbit show?]} detail
        tx [[:db/add (:db/id ellipse-orbit) :orbit/show-helper-lines? show?]]]
    (effects :tx tx)))

(defmethod handle-event :ellipse-orbit-tool/set-attr
  [_props {:keys [db]} {:event/keys [detail]}]
  (let [{:keys [ellipse-orbit-tool attr value]} detail
        planet (d/pull db '[*] (get-in ellipse-orbit-tool [:selector/selected :db/id]))
        id (get-in planet [:celestial/orbit :db/id])
        tx [[:db/add id attr value]]]
    (effects :tx tx
             :event #:event{:action :astro-scene/refresh})))

