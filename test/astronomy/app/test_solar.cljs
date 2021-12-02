(ns astronomy.app.test-solar
  (:require
   [astronomy.app.solar :as app.solar]
   [astronomy.system.solar :as system.solar]
   [astronomy.lib.api :as api]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [integrant.core :as ig]
   [fancoil.core :as fancoil])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))


(def conn
  (:astronomy/conn app.solar/system))


#_(api/save-db-file @conn "/private/frame/dev-20211202-1753.fra")

(def user-config
  app.solar/user-config)

(def config
  (fancoil/merge-config system.solar/default-config user-config))


(def system2
  (ig/init config ))

(keys system2)

(ig/halt! system2)