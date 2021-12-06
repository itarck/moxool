(ns astronomy.fixture
  (:require
   [datascript.transit :as dt]
   [astronomy.conn.core :as conn.core])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))



(def test-db1
  (->>
   (read-resource "frame/test/test-db1.fra")
   (dt/read-transit-str)))


(def test-conn1
  (conn.core/create-conn-from-db test-db1))


