(ns film2.modules.cinema.s
  (:require
   [astronomy.service.effect :as fx]))


(defmulti handle-event (fn [_ _ event] (:event/action event)))


(defmethod handle-event :cinema/change-ioframe
  [props env {{:keys [cinema ioframe-name]} :event/detail}]
  (let [effects (fx/effects :tx [#:cinema {:db/id (:db/id cinema)
                                           :current-ioframe-name ioframe-name}]
                            :event #:event {:action :editor/change-current-ioframe
                                            :detail {:editor (:cinema/editor cinema)
                                                     :ioframe {:db/id [:ioframe/name ioframe-name]}}}
                            :event #:event {:action :editor/load-current-ioframe
                                            :detail {:editor (:cinema/editor cinema)}})]
    effects)
  
  
  )