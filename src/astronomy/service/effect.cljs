(ns astronomy.service.effect
  (:require
   [posh.reagent :as p]
   [datascript.core :as d]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))



(defn create-effect [action detail]
  #:effect {:action action
            :detail detail})


(defn handle-effect! [effect env]
  (let [{:effect/keys [action detail]} effect
        {:keys [conn service-chan]} env]
    (case action
      :tx (p/transact! conn detail)
      :event (go (>! service-chan detail))
      :log (println "logging: " detail))))


(defn wrap-handle-event! [handle-event]
  (fn [props env event]
    (let [effect (handle-event props env event)]
      (handle-effect! effect env))))
