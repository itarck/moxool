(ns astronomy.model.test-astronomical-coordinate
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [datascript.core :as d]
   [astronomy.objects.planet.m :as planet]
   [astronomy.model.satellite :as satellite]
   [astronomy.model.coordinate :as coordinate]
   [astronomy.scripts.test-conn :refer [test-db11]]))


(def earth {:db/id [:planet/name "earth"]})
(def moon {:db/id [:satellite/name "moon"]})

(def astro-coor
  (d/pull test-db11 '[*] [:coordinate/name "赤道天球坐标系"]))


(deftest test-coordinate-1
  (is (= (->>
          (planet/cal-system-position test-db11 earth 2)
          (coordinate/from-system-position-at-epoch test-db11 astro-coor 2))
         [0 0 0]))
  (is (=  (->>
           (satellite/cal-system-position test-db11 moon 0)
           (coordinate/from-system-position-at-epoch test-db11 astro-coor 0))
          [-0.9209922249495435 -0.2626882147761478 -1.034279284990177]))

  (is (= (->>
          (satellite/cal-system-position test-db11 moon 5)
          (coordinate/from-system-position-at-epoch test-db11 astro-coor 5))
         [-1.2932436036575155 -0.5069960718801099 0.29847131548032735])))



(run-tests)