(ns astronomy.app.test-solar
  (:require
   [astronomy.app.solar :as solar]
   [astronomy.lib.api :as api]))


(def conn
  (:astronomy/conn solar/system))


(api/save-db-file @conn "/private/frame/dev-20211202-1702.fra")