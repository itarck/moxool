(ns astronomy.service.clock-tool
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]
   [astronomy.model.clock :as m.clock]
   [astronomy.model.user.clock-tool :as m.clock-tool]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :clock-tool/set-time-in-days
  [props {:keys [conn service-chan]} {:event/keys [detail]}]
  (let [{:keys [clock time-in-days]} detail
        new-clock {:db/id (:db/id clock)
                   :clock/time-in-days time-in-days}]
    (p/transact! conn (m.clock/set-clock-time-in-days-tx (:db/id clock) time-in-days))
    (go (>! service-chan #:event{:action :astro-scene/refresh}))
    (go (>! service-chan #:event{:action :clock.pub/time-changed
                                 :detail {:clock new-clock}}))))


(defmethod handle-event! :clock-tool/reset
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [clock-tool]} detail
        tx [[:db/add (:db/id clock-tool) :clock-tool/status :stop]
            [:db/add (-> clock-tool :clock-tool/clock :db/id) :clock/time-in-days 0]]]
    (p/transact! conn tx)))


(defmethod handle-event! :clock-tool/start
  [props {:keys [conn service-chan]} {:event/keys [detail]}]
  (go (let [clock-tool-id (get-in detail [:clock-tool :db/id])]
        (p/transact! conn [[:db/add clock-tool-id :clock-tool/status :start]])
        (loop []
          (let [clock-tool (m.clock-tool/pull-clock-tool @conn clock-tool-id)
                time-in-days (m.clock-tool/cal-next-step clock-tool (:clock-tool/clock clock-tool))
                {:clock-tool/keys [steps-per-second status]} clock-tool
                step-timeout (/ 1000 steps-per-second)]
            (<! (timeout step-timeout))
            (go (>! service-chan #:event{:action :clock-tool/count-event}))
            (case status
              :start (do
                       (go (>! service-chan #:event{:action :clock-tool/set-time-in-days
                                                    :detail {:clock (:clock-tool/clock clock-tool)
                                                             :time-in-days time-in-days}}))
                       (recur))
              :stop :stop
              nil))))))

(defmethod handle-event! :clock-tool/stop
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [clock-tool-id (get-in detail [:clock-tool :db/id])]
    (p/transact! conn [[:db/add clock-tool-id :clock-tool/status :stop]])))


(defmethod handle-event! :clock-tool/change-days-per-step
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [days-per-step]} detail
        clock-tool-id (get-in detail [:clock-tool :db/id])]
    (p/transact! conn [[:db/add clock-tool-id :clock-tool/days-per-step days-per-step]])))


(defmethod handle-event! :clock-tool/change-step-interval
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [clock-tool step-interval days-per-step]} detail
        days-per-step-1 (case step-interval
                          :minute (/ 1 24.0 60.0)
                          :hour (/ 1 24.0)
                          :star-day 0.99726968
                          :day 1
                          :30day 30
                          :year 365
                          :100year (* 365.25 100)
                          :custom days-per-step)
        tx (if days-per-step-1
             [{:db/id (:db/id clock-tool)
               :clock-tool/step-interval step-interval
               :clock-tool/days-per-step days-per-step-1}]
             [{:db/id (:db/id clock-tool)
               :clock-tool/step-interval step-interval}])]
    (p/transact! conn tx)))


(defmethod handle-event! :clock-tool/change-steps-per-second
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [steps-per-second clock-tool]} detail]
    (p/transact! conn [[:db/add (:db/id clock-tool) :clock-tool/steps-per-second steps-per-second]])))


(defmethod handle-event! :clock-tool/next-step
  [props {:keys [conn service-chan]} {:event/keys [detail]}]
  (let [{:keys [clock-tool clock]} detail
        time-in-days (m.clock-tool/cal-next-step clock-tool clock)]
    (go (>! service-chan #:event {:action :clock-tool/set-time-in-days
                                  :detail {:clock clock
                                           :time-in-days time-in-days}}))))

(defmethod handle-event! :clock-tool/prev-step
  [props {:keys [conn service-chan]} {:event/keys [detail]}]
  (let [{:keys [clock-tool clock]} detail
        time-in-days (m.clock-tool/cal-prev-step clock-tool clock)]
    (go (>! service-chan #:event {:action :clock-tool/set-time-in-days
                                  :detail {:clock clock
                                           :time-in-days time-in-days}}))))

(defmethod handle-event! :clock-tool/count-event
  [props {:keys [state-atom]} {:event/keys [detail]}]
  (swap! state-atom update :clock-tool/count-event inc)
  (when (= (mod (:clock-tool/count-event @state-atom) 100) 0)
    (let [d2 (new js/Date)
          d1 (:clock-tool/count-event-clock @state-atom)]
      (when d1
        (println "clock count event 100 times" (- d2 d1) "mseconds"))
      (swap! state-atom assoc :clock-tool/count-event-clock d2))))


(defn init-service! [props {:keys [process-chan] :as env}]
  (println "clock-control started")
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))


