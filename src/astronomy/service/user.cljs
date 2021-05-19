(ns astronomy.service.user
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout]]
   [methodology.model.user.person :as m.person]))


(defn init-service! [props {:keys [process-chan service-chan conn meta-atom]}]
  (let [{:keys [user]} props]
    (println "clock-control started")
    (go-loop []
      (let [{:event/keys [action detail] :as event} (<! process-chan)
            mode (if meta-atom (:mode @meta-atom) :read-and-write)]
        ;; (println event)
        (when (= mode :read-and-write)
          (try
            (case action
              :user/active-backpack-cell (let [{:keys [user cell backpack]} detail]
                                           (p/transact! conn [[:db/add (:db/id backpack) :backpack/active-cell (:db/id cell)]])
                                           (go (>! process-chan #:event{:action :user/select-tool
                                                                        :detail {:user user
                                                                                 :tool (:backpack-cell/tool cell)}})))
              :user/select-tool (let [{:keys [user tool]} detail]
                                  (if tool
                                    (p/transact! conn [[:db/add (:db/id user) :person/right-tool (:db/id tool)]])
                                    (p/transact! conn [[:db.fn/retractAttribute (:db/id user) :person/right-tool]])))

              :user/object-clicked (let [person (m.person/pull2 @conn (:db/id user))
                                         current-tool (:person/right-tool person)]
                                     (go (>! service-chan #:event {:action (keyword (:entity/type current-tool) :object-clicked)
                                                                   :detail (assoc detail :current-tool current-tool)}))))
            (catch js/Error e
              (js/console.log e)))))
      (recur))))