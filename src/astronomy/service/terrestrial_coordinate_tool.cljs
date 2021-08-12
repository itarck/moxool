(ns astronomy.service.terrestrial-coordinate-tool
  (:require
   [posh.reagent :as p]
   [astronomy.model.terrestrial-coordinate :as m.terrestrial-coordinate]
   [astronomy.model.user.terrestrial-coordinate-tool :as m.terrestrial-coordinate-tool]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :terrestrial-coordinate-tool/log
  [props {:keys [conn]} {:event/keys [detail]}]
  (println "terrestrial-cooridnate-tool/log: " detail))

(defmethod handle-event! :terrestrial-coordinate-tool/change-show-longitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [terrestrial-coordinate show?]} detail]
    (p/transact! conn (m.terrestrial-coordinate/change-show-longitude-tx terrestrial-coordinate show?))))

(defmethod handle-event! :terrestrial-coordinate-tool/change-show-latitude
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [terrestrial-coordinate show?]} detail]
    (p/transact! conn (m.terrestrial-coordinate/change-show-latitude-tx terrestrial-coordinate show?))))

(defmethod handle-event! :terrestrial-coordinate-tool/change-query-args
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [tool query-args]} detail
        tx (m.terrestrial-coordinate-tool/update-query-args-tx @conn tool query-args)]
    (p/transact! conn tx)))

(defmethod handle-event! :terrestrial-coordinate-tool/change-show-latitude-0
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [terrestrial-coordinate show?]} detail
        tx [{:db/id (:db/id terrestrial-coordinate)
             :terrestrial-coordinate/show-latitude-0? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :terrestrial-coordinate-tool/change-show-longitude-0
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [terrestrial-coordinate show?]} detail
        tx [{:db/id (:db/id terrestrial-coordinate)
             :terrestrial-coordinate/show-longitude-0? show?}]]
    (p/transact! conn tx)))

(defmethod handle-event! :terrestrial-coordinate-tool/change-show-regression-line
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [terrestrial-coordinate show?]} detail
        tx [{:db/id (:db/id terrestrial-coordinate)
             :terrestrial-coordinate/show-regression-line? show?}]]
    (p/transact! conn tx)))


(defn init-service! [props {:keys [process-chan] :as env}]
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))


