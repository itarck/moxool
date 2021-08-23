(ns astronomy.model.test-astronomical-coordinate
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [datascript.core :as d]
   [astronomy.objects.planet.m :as planet]
   [astronomy.model.satellite :as satellite]
   [astronomy.objects.astronomical-coordinate.m :as ac.m]
   [astronomy.scripts.test-conn :refer [test-db3 test-db11]]))



(def earth {:db/id [:planet/name "earth"]})
(def moon {:db/id [:satellite/name "moon"]})
(def ac-1 {:db/id [:coordinate/name "赤道天球坐标系"]})


(def astro-coor
  (d/pull test-db11 '[*] [:coordinate/name "赤道天球坐标系"]))

(ac.m/convert-to-coordinate-position test-db11 ac-1 2 (planet/cal-system-position test-db11 earth 2))


(let [earth {:db/id [:planet/name "earth"]}
      ac-1 {:db/id [:coordinate/name "赤道天球坐标系"]}
      epoch-days 5
      system-position (satellite/cal-system-position test-db11 moon epoch-days)
      coordinate-position (ac.m/convert-to-coordinate-position test-db11 ac-1 epoch-days system-position)]
  coordinate-position)