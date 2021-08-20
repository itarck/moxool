(ns astronomy.test-performance
  (:require
   [astronomy.app.core :as app]
   [cljs.core.async :refer [go >!]]
   [posh.reagent :as p]
   [film2.system.studio :as studio]))


;; current studio in app core

(comment
  (def solar-system1 (:scene-system @(:studio/instance-atom app/studio)))


  (def conn (get-in solar-system1 [:system/conn]))
  (def service-chan (get-in solar-system1 [:system/service-chan]))


  (def clock @(p/pull conn '[*] [:clock/name "default"]))
  (def clock-tool @(p/pull conn '[*] [:tool/name "clock control 1"]))


  (go (>! service-chan #:event{:action :clock-tool/start
                               :detail {:clock-tool clock-tool
                                        :clock clock}}))

  (:clock/time-in-days @(p/pull conn '[*] [:clock/name "default"]))


  (go (>! service-chan #:event{:action :clock-tool/stop
                               :detail {:clock-tool clock-tool
                                        :clock clock}})))



;; 

(comment

  (def studio-2 (studio/create-app! {}))

  (def solar-system2 (:scene-system @(:studio/instance-atom studio-2)))


  (def conn2 (get-in solar-system2 [:system/conn]))
  (def service-chan2 (get-in solar-system2 [:system/service-chan]))


  (def clock2 @(p/pull conn2 '[*] [:clock/name "default"]))
  (def clock-tool2 @(p/pull conn2 '[*] [:tool/name "clock control 1"]))

  (go (>! service-chan2 #:event{:action :clock-tool/start
                                :detail {:clock-tool clock-tool
                                         :clock clock}}))

  (go (>! service-chan2 #:event{:action :clock-tool/stop
                                :detail {:clock-tool clock-tool
                                         :clock clock}})))
