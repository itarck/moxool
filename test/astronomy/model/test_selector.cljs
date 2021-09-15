(ns astronomy.model.test-selector
  (:require
   [cljs.spec.alpha :as s]
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [datascript.core :as d]
   [astronomy.objects.planet.m]
   [astronomy.conn.mini-factory :as mini-factory]))


(def sample
  {:selector/candinates #{{:db/id [:planet/name "earth"]}
                          {:db/id [:star/name "sun"]}}
   :selector/selected [:planet/name "earth"]
   :tool/name "selector-tool"
   :tool/chinese-name "行星"
   :tool/icon "/image/pirate/earth.jpg"
   :tool/type :selector-tool
   :entity/type :selector-tool})


(def test-db
  (d/db-with (mini-factory/create-db1) [sample]))


(:selector/selected (d/pull test-db '[*] [:tool/name "selector-tool"]))
;; => #:db{:id 23}

(:selector/candinates (d/pull test-db '[*] [:tool/name "selector-tool"]))
;; => [#:db{:id 20} #:db{:id 23}]
