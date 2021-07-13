(ns shu.calendar.timestamp
  (:require
   [applied-science.js-interop :as j]
   [cljs-time.core :refer [now]]))

;; UTC timestamp in milliseconds

(defn current-timestamp! []
  (j/call (new js/Date) :getTime))

