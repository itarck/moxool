(ns film2.parts.process
  (:require
   [cljs.core.async :refer [go >! <!]]
   [integrant.core :as ig]))


(defmethod ig/init-key :cinema/init-process
  [_ {:keys [service-chan]}]
  (go (>! service-chan #:event {:action :editor/load-current-ioframe
                                :detail {:editor {:db/id [:editor/name "default"]}}})))

