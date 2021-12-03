(ns film2.parts.process
  (:require
   [cljs.core.async :refer [go >! <! timeout]]
   [integrant.core :as ig]))


(defmethod ig/init-key :cinema/init-process
  [_ {:keys [service-chan]}]
  (go (>! service-chan #:event {:action :cinema/login-from-localstorage
                                :detail {:cinema {:db/id [:cinema/name "default"]}}})))


