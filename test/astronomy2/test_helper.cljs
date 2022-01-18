(ns astronomy2.test-helper
  (:require
   [astronomy2.system :as sys]
   [astronomy2.plugin.star]
   [astronomy2.app :as app]
   [astronomy2.db :as db]))


(def test-db
  (db/create-db :test-db))

(defonce instance
  (sys/init {::sys/pconn {:initial-db test-db}}))

(def spec (::sys/spec instance))

(def model (::sys/model instance))

(def subscribe (::sys/subscribe instance))

(def process (::sys/process instance))

(def homies app/homies)

