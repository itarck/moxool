(ns astronomy.app.test-solar-system
  (:require 
   [cljs.core.async :refer [go >!]]
   [posh.reagent :as p]
   [astronomy.system.solar :as solar]
   [astronomy.scripts.test-conn :as test-conn]))


(def solar-system1 
  (solar/create-system! {:initial-db test-conn/test-db11}))


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
                                      :clock clock}}))