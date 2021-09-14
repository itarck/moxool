(ns astronomy.space.keyboard.s
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]))


(defn init-keyboard-listener! [props {:keys [service-chan] :as env}]
  (j/call js/document
          :addEventListener "keydown"
          (fn [e]
            (let [event #:event {:action :user/keyboard-down
                                 :detail {:key (j/get e :key)
                                          :alt-key (j/get-in e [:altKey])
                                          :meta-key (j/get-in e [:metaKey])
                                          :shift-key (j/get-in e [:shiftKey])}}]

              (go (>! service-chan event))))))


(defn init-service! [props env]
  (init-keyboard-listener! props env))

