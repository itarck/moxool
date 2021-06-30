(ns astronomy.service.horizontal-coordinate-tool
  (:require
   [posh.reagent :as p]
   [datascript.core :as d]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   [astronomy.model.horizontal-coordinate :as m.horizon]
   [astronomy.model.user.horizontal-coordinate-tool :as m.horizon-tool]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :horizontal-coordinate-tool/log
  [props {:keys [conn]} {:event/keys [detail]}]
  (println "horizontal-cooridnate-tool/log: " detail))

(defmethod handle-event! :horizontal-coordinate-tool/change-show-longitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail]
    (p/transact! conn (m.horizon/change-show-longitude-tx (:tool/target tool) show?))))

(defmethod handle-event! :horizontal-coordinate-tool/change-show-latitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail]
    (p/transact! conn (m.horizon/change-show-latitude-tx (:tool/target tool) show?))))

(defmethod handle-event! :horizontal-coordinate-tool/change-show-compass
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail]
    (p/transact! conn (m.horizon/change-show-compass-tx (:tool/target tool) show?))))

(defmethod handle-event! :horizontal-coordinate-tool/change-show-horizontal-plane
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail]
    (p/transact! conn (m.horizon/change-show-horizontal-plane-tx (:tool/target tool) show?))))

(defmethod handle-event! :horizontal-coordinate-tool/change-radius
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool radius]} detail]
    (p/transact! conn (m.horizon/change-radius-tx (:tool/target tool) radius))))


(defmethod handle-event! :horizontal-coordinate-tool/update-default
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [spaceship-camera-control (d/pull @conn '[*] (get-in detail [:spaceship-camera-control :db/id]))
        astro-scene (d/pull @conn '[*] (get-in props [:astro-scene :db/id]))
        position-in-scene (m.spaceship/get-landing-position-in-scene spaceship-camera-control astro-scene)
        tx (m.horizon/set-position-tx {:db/id [:horizontal-coordinate/name "default"]} position-in-scene)]
    ;; (println "horizontal-coordinate-tool/update-default: " position-in-scene)
    (p/transact! conn tx)))

(defmethod handle-event! :horizontal-coordinate-tool/change-query-args
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool query-args]} detail
        chinese-name (first query-args)]
    (p/transact! conn (m.horizon-tool/change-query-args-tx tool query-args))
    (when-not (= chinese-name "未选择")
      (let [tx (m.horizon-tool/change-target-tx tool {:db/id [:horizontal-coordinate/chinese-name chinese-name]})]
        ;; (println :horizontal-coordinate-tool/change-query-args tx)
        (p/transact! conn tx)))))


(defn init-service! [props {:keys [process-chan] :as env}]
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))


