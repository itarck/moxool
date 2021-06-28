(ns astronomy.app.test-service-only
  (:require
   [posh.reagent :as p]
   [cljs.core.async :refer [go >! <!]]
   [integrant.core :as ig]
   [astronomy.service.clock-tool :as s.clock-tool]
   [astronomy.app.scene-free :as scene-free]))



(def free-app-instance (scene-free/create-app! {}))

(def conn (get-in free-app-instance [:app/scene-conn]))


(:clock/time-in-days @(p/pull conn '[*] [:clock/name "default"]))
;; => 0

(time
 (doseq [t (range 1000)]
   (s.clock-tool/handle-event! {} {:conn conn} #:event{:action :clock-tool/set-time-in-days
                                                       :detail {:clock {:db/id [:clock/name "default"]}
                                                                :time-in-days t}})))


(:clock/time-in-days @(p/pull conn '[*] [:clock/name "default"]))
;; => 1
