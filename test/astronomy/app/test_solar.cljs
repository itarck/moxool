(ns astronomy.app.test-solar
  (:require
   [astronomy.app.solar :as solar]
   [astronomy.lib.api :as api]
   [datascript.core :as d]))


(def conn
  (:astronomy/conn solar/system))


#_(api/save-db-file @conn "/private/frame/dev-20211202-1702.fra")


(:camera/position (d/pull @conn '[*] [:camera/name "default"]))