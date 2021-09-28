(ns astronomy.app.test-app-core
  (:require
   [astronomy.app.core :as app]
   [cljs.core.async :refer [go >!]]
   [posh.reagent :as p]
   [datascript.core :as d]))


;; current studio in app core


(def system app/ioframe-system)

(keys system)
;; => (:ioframe-system/view :ioframe-system/conn :ioframe-system/dom-atom :ioframe-system/meta-atom)

(def conn (:ioframe-system/conn system))

(def service-chan (:ioframe-system/service-chan system))

(defn dispatch [event-vector]
  (let [[action detail] event-vector
        event #:event{:action action
                      :detail detail}]
    (go (>! service-chan event))))



(let [clock-atom (p/pull conn '[*] [:clock/name "default"])]
  (dispatch [:clock-tool/set-time-in-days
             {:clock @clock-atom
              :time-in-days 1}])
  clock-atom)