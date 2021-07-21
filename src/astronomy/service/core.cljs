(ns astronomy.service.core
  (:require
   [cljs.core.async :as async :refer [go >! <! chan go-loop]]
   [methodology.service.camera :as s.camera]
   [methodology.service.mouse :as s.mouse]
   [astronomy.service.user :as s.user]
   [astronomy.service.astro-scene :as s.astro-scene]
   [astronomy.service.universe-tool :as s.universe-tool]
   [astronomy.service.clock-tool :as s.clock-tool]
   [astronomy.service.info-tool :as s.info-tool]
   [astronomy.service.spaceship-camera-control :as s.spaceship]
   [astronomy.service.ppt-tool :as s.ppt-tool]
   [astronomy.service.horizontal-coordinate-tool :as s.hct]
   [astronomy.service.goto-celestial-tool :as s.goto-tool]
   [astronomy.service.equatorial-coordinate-tool :as s.ect]
   [astronomy.service.contellation-tool :as s.constellation-tool]
   [astronomy.service.atmosphere-tool :as s.atmosphere-tool]
   [astronomy.service.horizon-coordinate-tool :as s.horizon-coordinate]))


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
   {:listen [:ppt-tool]
    :process-name "ppt-tool"
    :service-fn s.ppt-tool/init-service!}
   {:listen [:horizontal-coordinate-tool]
    :process-name "horizontal-coordinate-tool"
    :service-fn s.hct/init-service!}
   {:listen [:equatorial-coordinate-tool]
    :process-name "equatorial-coordinate-tool"
    :service-fn s.ect/init-service!}
   {:listen [:goto-celestial-tool]
    :process-name "goto-celestial-tool"
    :service-fn s.goto-tool/init-service!}
   {:listen [:constellation-tool]
    :process-name "constellation-tool"
    :service-fn s.constellation-tool/init-service!}
   {:listen [:atmosphere-tool]
    :process-name "atmosphere-tool"
    :service-fn s.atmosphere-tool/init-service!}
   {:listen [:horizon-coordinate]
    :process-name "horizon-coordinate"
    :service-fn s.horizon-coordinate/init-service!}


   {:listen []
    :process-name "camera"
    :service-fn s.camera/init-service!}
   {:listen [:mouse]
    :process-name "mouse"
    :service-fn s.mouse/init-service!}])


(defn init-service-center! [props env]
  ;; (println "!!!!! init service-center......")
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

