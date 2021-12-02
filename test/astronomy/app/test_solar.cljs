(ns astronomy.app.test-solar
  (:require
   [astronomy.app.solar :as solar]
   [astronomy.lib.api :as api]
   [datascript.core :as d]
   [datascript.transit :as dt])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))


(def conn
  (:astronomy/conn solar/system))


(api/save-db-file @conn "/private/frame/dev-20211202-1753.fra")


(:camera/position (d/pull @conn '[*] [:camera/name "default"]))

(:spaceship-camera-control/position (d/pull @conn '[*] [:spaceship-camera-control/name "default"]))


(def db
  (dt/read-transit-str (read-resource "private/frame/dev-20211202-1735.fra")))

(:camera/position (d/pull db '[*] [:camera/name "default"]))

(:spaceship-camera-control/position (d/pull db '[*] [:spaceship-camera-control/name "default"]))