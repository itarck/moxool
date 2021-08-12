(ns astronomy.service.effect
  (:require
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]))



(defn create-effect [action detail]
  #:effect {:action action
            :detail detail})

(defn create-effects
  [& action-details]
  (let [ads (partition 2 action-details)]
    (map (fn [[a d]] (create-effect a d)) ads)))

(def effect create-effect)

(def effects create-effects)


(defn handle-effect! [effect env]
  (let [{:effect/keys [action detail]} effect
        {:keys [conn service-chan]} env]
    (case action
      :tx (p/transact! conn detail)
      :event (go (>! service-chan detail))
      :log (println "logging: " detail))))
