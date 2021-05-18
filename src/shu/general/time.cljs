(ns shu.general.time
  (:require
   [applied-science.js-interop :as j]))


(defn get-timestamp []
  (j/call (new js/Date) :getTime))

(defn get-timestring []
  (j/call (new js/Date) .toLocaleString))


