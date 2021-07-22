(ns astronomy.service.astronomical-coordinate-tool
  (:require
   [posh.reagent :as p]
   [datascript.core :as d]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.astronomical-coordinate :as m.astronomical-coordinate]
   [astronomy.model.user.astronomical-coordinate-tool :as m.astronomical-coordinate-tool]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :astronomical-coordinate-tool/log
  [props {:keys [conn]} {:event/keys [detail]}]
  (println "horizontal-cooridnate-tool/log: " detail))

(defmethod handle-event! :astronomical-coordinate-tool/change-show-longitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail]
    (p/transact! conn (m.astronomical-coordinate/change-show-longitude-tx astronomical-coordinate show?))))

(defmethod handle-event! :astronomical-coordinate-tool/change-show-latitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail]
    (p/transact! conn (m.astronomical-coordinate/change-show-latitude-tx astronomical-coordinate show?))))

(defmethod handle-event! :astronomical-coordinate-tool/set-scene-reference
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate]} detail
        astro-scene (get-in props [:astro-scene])]
    (p/transact! conn (m.astro-scene/set-scene-coordinate-tx astro-scene astronomical-coordinate))))

(defmethod handle-event! :astronomical-coordinate-tool/change-query-args
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool query-args]} detail
        tx (m.astronomical-coordinate-tool/update-query-args-tx @conn tool query-args)]
    (p/transact! conn tx)))

(defmethod handle-event! :astronomical-coordinate-tool/change-show-latitude-0
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-latitude-0? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :astronomical-coordinate-tool/change-show-regression-line
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-regression-line? show?}]]
    (p/transact! conn tx)))


(defmethod handle-event! :astronomical-coordinate-tool/change-show-longitude-0
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-longitude-0? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :astronomical-coordinate-tool/change-show-ecliptic
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-ecliptic? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :astronomical-coordinate-tool/change-show-lunar-orbit
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-lunar-orbit? show?}]]
    (p/transact! conn tx)))

(defn init-service! [props {:keys [process-chan] :as env}]
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))


