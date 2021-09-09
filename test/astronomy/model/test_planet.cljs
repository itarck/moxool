(ns astronomy.model.test-planet
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [datascript.core :as d]
   [astronomy.objects.planet.m :as planet]
   [astronomy.objects.planet.h :as planet.h :refer [handle-event]]
   [astronomy.test-db :refer [test-db-full]]))

(def test-db test-db-full)

;; test model

(def earth (d/pull test-db '[*] [:planet/name "earth"]))
(def mercury (d/pull test-db '[*] [:planet/name "mercury"]))

(def ac-1 (d/pull test-db '[*] [:coordinate/name "赤道天球坐标系"]))


(deftest test-planet-1
  (let [earth (d/pull test-db '[*] [:planet/name "earth"])]
    (is (= (planet/cal-system-position-at-epoch test-db earth 2)
           [439.7765559273832 190.30873698560643 -105.51499959752967]))
    (is (= (planet/cal-system-position-at-epoch test-db earth 0)
           [442.9497885783528 191.68192377598768 -88.40464973856325]))
    (is (= (d/q planet/query-all-ids test-db)
           [23 32 36 40 44 48]))

    ))

;; test handler

(handle-event {} {} #:event {:action :planet/show-orbit
                             :detail {:celestial earth
                                      :show? true}})
;; => (#:effect{:action :tx, :detail [{:db/id 26, :orbit/show? true}]})


(run-tests)

;; 集成测试

