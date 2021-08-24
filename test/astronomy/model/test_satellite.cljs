(ns astronomy.model.test-satellite
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [datascript.core :as d]
   [astronomy.objects.satellite.m :as m.satellite]
   [astronomy.scripts.test-conn :refer [test-db3 test-db11]]))



(def moon (d/pull test-db11 '[*] [:satellite/name "moon"]))

(deftest test-satellite-1
  (let [moon (d/pull test-db11 '[*] [:satellite/name "moon"])]
    (is (= (m.satellite/cal-local-position-at-epoch test-db11 moon 0)
           '(-0.92099222494957 -0.2626882147761619 -1.034279284990182)))
    (is (= (m.satellite/cal-local-position-at-epoch test-db11 moon 1)
           '(-1.1051375691915346 -0.3484879007849228 -0.8180533688332752)))
    (is (= (m.satellite/cal-system-position-at-epoch test-db11 moon 0)
           [442.02879635340327 191.41923556121154 -89.43892902355343]))
    (is (= (m.satellite/cal-system-position-at-epoch test-db11 moon 1)
           [440.3267309888779 190.6765700928551 -97.79296943774905]))
    ))


(run-tests)