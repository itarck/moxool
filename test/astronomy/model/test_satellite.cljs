(ns astronomy.model.test-satellite
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [cljs.core.async :refer [go >!]]
   [datascript.core :as d]
   [astronomy.objects.satellite.m :as m.satellite]
   [astronomy.scripts.test-conn :refer [test-db12 real-db]]
   [astronomy.app.core :as app.core]))


(def test-db test-db12)


(def moon (d/pull test-db '[*] [:satellite/name "moon"]))

(def europa (d/pull test-db '[*] [:satellite/name "europa"]))

(:object/position europa)
;; => (2.0056184322477315 0.9600069003828832 0.24303543742713574)

(def europa2 (d/pull test-db '[*] [:satellite/name "europa"]))

(:object/position europa2)
;; => (2.0056184322477315 0.9600069003828832 0.24303543742713574)



(deftest test-satellite-1
  (let [moon (d/pull test-db '[*] [:satellite/name "moon"])]
    (is (= (m.satellite/cal-local-position-at-epoch test-db moon 0)
           '(-0.92099222494957 -0.2626882147761619 -1.034279284990182)))
    (is (= (m.satellite/cal-local-position-at-epoch test-db moon 1)
           '(-1.1051375691915346 -0.3484879007849228 -0.8180533688332752)))
    (is (= (m.satellite/cal-system-position-at-epoch test-db moon 0)
           [442.02879635340327 191.41923556121154 -89.43892902355343]))
    (is (= (m.satellite/cal-system-position-at-epoch test-db moon 1)
           [440.3267309888779 190.6765700928551 -97.79296943774905]))))


(def app app.core/app)

(def conn (get-in app [:system/conn]))

(def service-chan (get-in app [:system/service-chan]))

(go (>! service-chan #:event{:action :clock-tool/set-time-in-days
                             :detail {:clock  {:db/id [:clock/name "default"]} 
                                      :time-in-days 50}}))

(def europa-1 (d/pull @conn '[*] [:satellite/name "europa"]))

(def earth (d/pull @conn '[*] [:planet/name "earth"]))


(:object/position europa-1)
;; => (0.7692536733752218 0.3999984143313179 2.061900779118907)

;; => (1.870801373747469 0.8779962587544651 -0.8558093000182607)


(run-tests)


(comment

  (d/pull test-db '[*] [:satellite/name "moon"])

  (:celestial/orbit (d/pull test-db '[*] [:satellite/name "newton-apple"])))