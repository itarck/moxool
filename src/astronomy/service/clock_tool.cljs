(ns astronomy.service.clock-tool
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]
   [astronomy.model.clock :as m.clock]
   [astronomy.model.user.clock-tool :as m.clock-tool]))


(defn go-ticks! [conn clock-tool-id service-chan]
  (go-loop []
    (let [clock-tool (m.clock-tool/pull-clock-tool @conn clock-tool-id)
          time-in-days (m.clock-tool/tick-clock clock-tool)
          {:clock-tool/keys [steps-per-second status]} clock-tool
          step-timeout (/ 1000 steps-per-second)]
      (<! (timeout step-timeout))
      (case status
        :start (do 
                 (go (>! service-chan #:event{:action :clock-tool/set-time-in-days
                                              :detail {:clock (:clock-tool/clock clock-tool)
                                                       :time-in-days time-in-days}}))
                 (recur)) 
        :stop :stop
        nil))))


(defn init-service! [props {:keys [process-chan service-chan conn]}]
  (let [{:keys [user]} props]
    (println "clock-control started")
    (go-loop []
      (let [{:event/keys [action detail] :as event} (<! process-chan)
            {:keys [clock clock-tool]} detail]
        ;; (println event)
        (try
          (case action

            :clock-tool/set-time-in-days (let [{:keys [time-in-days]} detail
                                               db1 @conn
                                               tx1 (m.clock/set-clock-time-in-days-tx clock time-in-days)
                                               db2 (d/db-with db1 tx1)
                                               tx2 (m.clock/update-celestial-by-clock-tx db2 (:db/id clock))
                                               db3 (d/db-with db2 tx2)
                                               tx3 (m.clock/update-reference-tx db3)]
                                           (p/transact! conn (concat tx1 tx2 tx3)))

            :clock-tool/update-celestial (let [tx (m.clock/update-celestial-by-clock-tx @conn (:db/id clock))]
                                           (p/transact! conn tx))

            :clock-tool/update-reference (let [tx (m.clock/update-reference-tx @conn)]
                                           (p/transact! conn tx))

            :clock-tool/reset (let [tx [[:db/add (:db/id clock-tool) :clock-tool/status :stop]
                                        [:db/add (-> clock-tool :clock-tool/clock :db/id) :clock/time-in-days 0]]]
                                (p/transact! conn tx))

            :clock-tool/start (do
                                (p/transact! conn [[:db/add (:db/id clock-tool) :clock-tool/status :start]])
                                (go-ticks! conn (:db/id clock-tool) service-chan))


            :clock-tool/stop (p/transact! conn [[:db/add (:db/id clock-tool) :clock-tool/status :stop]])

            :clock-tool/change-days-per-step
            (let [{:keys [days-per-step]} detail]
              (p/transact! conn [[:db/add (:db/id clock-tool) :clock-tool/days-per-step days-per-step]]))

            :clock-tool/change-step-interval
            (let [{:keys [step-interval]} detail
                  days-per-step (case step-interval
                                  :minute (/ 1 24.0 60.0)
                                  :hour (/ 1 24.0)
                                  :star-day 0.99726968
                                  :day 1)
                  tx [{:db/id (:db/id clock-tool)
                       :clock-tool/step-interval step-interval
                       :clock-tool/days-per-step days-per-step}]]
              (p/transact! conn tx))

            :clock-tool/change-steps-per-second (let [{:keys [steps-per-second]} detail]
                                                  (p/transact! conn [[:db/add (:db/id clock-tool) :clock-tool/steps-per-second steps-per-second]]))
            :clock-tool/object-clicked (println "clock tool: objected clicked " event))
          

          (catch js/Error e
            (js/console.log e))))
      (recur))))

