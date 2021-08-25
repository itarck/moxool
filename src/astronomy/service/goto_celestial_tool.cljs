(ns astronomy.service.goto-celestial-tool
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [cljs.core.async :refer [go-loop go >! <! timeout] :as a]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.objects.star.m :as m.star]
   [astronomy.model.user.goto-celestial-tool :as m.goto-tool]))


(defmulti handle-event! (fn [props env event] (:event/action event)))


(defmethod handle-event! :goto-celestial-tool/goto
  [props {:keys [conn service-chan]} {:event/keys [detail]}]
  (let [{:keys [new-celestial-id goto-celestial-tool]} detail
        scc (get-in props [:spaceship-camera-control])
        astro-scene (d/pull @conn '[*] (get-in props [:astro-scene :db/id]))
        coordinate-id (get-in astro-scene [:astro-scene/coordinate :db/id])
        celestial (d/pull @conn '[*] new-celestial-id)
        radius (* 5 (:scene/scale astro-scene) (:celestial/radius celestial))
        tx (concat (m.goto-tool/set-target-tx goto-celestial-tool new-celestial-id)
                   (m.spaceship/set-position-tx scc [radius radius radius]))]
    (p/transact! conn tx)
    (go (>! service-chan #:event{:action :coordinate-tool/set-track-position
                                 :detail {:coordinate-id coordinate-id
                                          :track-position-id new-celestial-id}}))))

(defmethod handle-event! :goto-celestial-tool/show-orbit
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [celestial show?]} detail
        tx (m.celestial/update-show-orbit-tx celestial show?)]
    (p/transact! conn tx)))


(defmethod handle-event! :goto-celestial-tool/show-all-planet-orbit
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [celestial show?]} detail
        star (d/pull @conn '[{:planet/_star [*]}] (:db/id celestial))
        tx (m.star/show-all-planet-orbits-tx star show?)]
    (p/transact! conn tx)))

(defmethod handle-event! :goto-celestial-tool/show-spin-helper
  [props {:keys [conn]} {:event/keys [detail]}]
  (let [{:keys [celestial show?]} detail
        tx (m.celestial/update-show-spin-helper-tx celestial show?)]
    (p/transact! conn tx)))

(defmethod handle-event! :goto-celestial-tool/log
  [props {:keys [conn]} {:event/keys [detail]}]
  (println ":goto-celestial-tool/log" props detail))


(defn init-service! [props {:keys [process-chan] :as env}]
  (println "goto celestial started")
  (go-loop []
    (let [event (<! process-chan)]
      (try
        (handle-event! props env event)
        (catch js/Error e
          (js/console.log e))))
    (recur)))

