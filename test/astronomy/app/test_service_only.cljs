(ns astronomy.app.test-service-only
  (:require
   [posh.reagent :as p]
   [cljs.core.async :refer [go >! <!]]
   [integrant.core :as ig]
   [astronomy.app.free-mode :as free-app]))


(def system (ig/init free-app/config))


(def conn (::free-app/conn system))


(def service-chan (::free-app/chan system))


(:clock/time-in-days @(p/pull conn '[*] [:clock/name "default"]))
;; => 0


(go (>! service-chan #:event {:action :clock/set-time-in-days
                              :detail {:clock {:db/id [:clock/name "default"]}
                                       :time-in-days 1}}))

(:clock/time-in-days @(p/pull conn '[*] [:clock/name "default"]))
;; => 1


(let [earth @(p/pull conn '[*] [:planet/name "earth"])]
  (:object/position earth))

(go (>! service-chan #:event {:action :spaceship-camera-control/hello
                              :detail "hello"}))