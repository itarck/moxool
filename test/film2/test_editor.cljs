(ns film2.test_editor
  (:require
   [cljs.core.async :refer [go >! <!]]
   [astronomy.app.core :refer [studio]]))



(keys studio)

(def service-chan (:free-room/service-chan studio))

service-chan

(go (>! service-chan #:event {:action :editor/load-current-ioframe}))