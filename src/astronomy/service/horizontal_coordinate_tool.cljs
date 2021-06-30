(ns astronomy.service.horizontal-coordinate-tool
  (:require
   [posh.reagent :as p]
   [datascript.core :as d]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   [astronomy.model.horizontal-coordinate :as m.horizon]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :horizontal-coordinate-tool/log
  [props {:keys [conn]} {:event/keys [detail]}]
  (println "horizontal-cooridnate-tool/log: " detail))

(defmethod handle-event! :horizontal-coordinate-tool/change-show-longitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (get-in tool [:horizontal-coordinate-tool/target :db/id])
             :horizontal-coordinate/show-longitude? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :horizontal-coordinate-tool/change-show-latitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (get-in tool [:horizontal-coordinate-tool/target :db/id])
             :horizontal-coordinate/show-latitude? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :horizontal-coordinate-tool/change-show-compass
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (get-in tool [:horizontal-coordinate-tool/target :db/id])
             :horizontal-coordinate/show-compass? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :horizontal-coordinate-tool/change-show-horizontal-plane
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (get-in tool [:horizontal-coordinate-tool/target :db/id])
             :horizontal-coordinate/show-horizontal-plane? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :horizontal-coordinate-tool/change-radius
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool radius]} detail
        tx [{:db/id (get-in tool [:horizontal-coordinate-tool/target :db/id])
             :horizontal-coordinate/radius radius}]]
    (p/transact! conn tx)))


(defmethod handle-event! :horizontal-coordinate-tool/update-default
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [spaceship-camera-control (d/pull @conn '[*] (get-in detail [:spaceship-camera-control :db/id]))
        astro-scene (d/pull @conn '[*] (get-in props [:astro-scene :db/id]))
        position-in-scene (m.spaceship/get-landing-position-in-scene spaceship-camera-control astro-scene)
        tx (m.horizon/set-position-tx {:db/id [:horizontal-coordinate/name "default"]} position-in-scene)]
    (p/transact! conn tx)))


(defn init-service! [props {:keys [process-chan] :as env}]
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))


