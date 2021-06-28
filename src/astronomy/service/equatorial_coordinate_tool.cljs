(ns astronomy.service.equatorial-coordinate-tool
  (:require
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :equatorial-coordinate-tool/log
  [props {:keys [conn]} {:event/keys [detail]}]
  (println detail))


(defmethod handle-event! :equatorial-coordinate-tool/change-show-longitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (:db/id tool)
             :equatorial-coordinate-tool/show-longitude? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :equatorial-coordinate-tool/change-show-latitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (:db/id tool)
             :equatorial-coordinate-tool/show-latitude? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :equatorial-coordinate-tool/change-show-latitude-0
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (:db/id tool)
             :equatorial-coordinate-tool/show-latitude-0? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :equatorial-coordinate-tool/change-show-regression-line
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (:db/id tool)
             :equatorial-coordinate-tool/show-regression-line? show?}]]
    (p/transact! conn tx)))


(defmethod handle-event! :equatorial-coordinate-tool/change-show-longitude-0
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (:db/id tool)
             :equatorial-coordinate-tool/show-longitude-0? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :equatorial-coordinate-tool/change-show-ecliptic
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (:db/id tool)
             :equatorial-coordinate-tool/show-ecliptic? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :equatorial-coordinate-tool/change-show-lunar-orbit
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool show?]} detail
        tx [{:db/id (:db/id tool)
             :equatorial-coordinate-tool/show-lunar-orbit? show?}]]
    (p/transact! conn tx)))

(defn init-service! [props {:keys [process-chan] :as env}]
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))


