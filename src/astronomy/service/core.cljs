(ns astronomy.service.core
  (:require
   [cljs.core.async :as async :refer [go >! <! chan go-loop]]
   [methodology.service.camera :as s.camera]
   [astronomy.service.user :as s.user]
   [astronomy.service.astro-scene :as s.astro-scene]
   [astronomy.service.universe-tool :as s.universe-tool]
   [astronomy.service.clock-tool :as s.clock-tool]
   [astronomy.service.info-tool :as s.info-tool]
   [astronomy.service.spaceship-camera-control :as s.spaceship]
   [astronomy.service.coordinate-tool :as s.coordinate-tool]))


(def processes
  [{:listen [:user]
    :process-name "user"
    :service-fn s.user/init-service!}
   {:listen [:astro-scene]
    :process-name "astro-scene"
    :service-fn s.astro-scene/init-service!}

   {:listen [:universe-tool]
    :process-name "universe-tool"
    :service-fn s.universe-tool/init-service!}
   {:listen [:clock-tool]
    :process-name "clock-tool"
    :service-fn s.clock-tool/init-service!}
   {:listen [:info-tool]
    :process-name "info-tool"
    :service-fn s.info-tool/init-service!}
   {:listen [:spaceship-camera-control]
    :process-name "spaceship-camera-control"
    :service-fn s.spaceship/init-service!}
   {:listen [:coordinate-tool]
    :process-name "coordinate-tool"
    :service-fn s.coordinate-tool/init-service!}
   
   {:listen []
    :process-name "camera"
    :service-fn s.camera/init-service!}])


(defn init-service-center! [props env]
  (let [{:keys [service-chan]} env
        process-dispatch-fn (fn [event]
                              (keyword (namespace (:event/action event))))
        process-publication (async/pub service-chan process-dispatch-fn)]

    (doseq [{:keys [service-fn process-name listen]} processes]
      (let [process-chan (chan)]
        (doseq [l listen]
          (async/sub process-publication l process-chan))
        (service-fn props (-> env
                              (assoc :process-chan process-chan)
                              (assoc :process-name process-name)))))

    ;; (kick-start! env)
    
    {:service-chan service-chan}))

