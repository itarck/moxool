(ns astronomy.model.test-star
  (:require
   [datascript.core :as d]
   [datascript.transit :as dt]
   [astronomy.conn.core :as conn.core]
   [posh.reagent :as p]
   [astronomy.objects.star.m :as star.m])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))


(def test-db
  (->>
   (read-resource "frame/test/test-db1.fra")
   (dt/read-transit-str)))


(def test-conn 
  (conn.core/create-conn-from-db test-db))

(d/pull test-db '[*] [:star/name "sun"])

(d/q '[:find [?planet ...]
       :where
       [?planet :planet/star ?star]
       [?planet :object/scene _]
       :in $ ?star]
     test-db [:star/name "sun"])


(star.m/sub-planets test-conn {:db/id [:star/name "sun"]})