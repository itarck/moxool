(ns astronomy.model.test-celestial-group
  (:require
   [cljs.spec.alpha :as s]
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [datascript.core :as d]
   [shu.three.vector3 :as v3]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.objects.planet.m]
   [astronomy.objects.celestial-group.m :as celestial-group.m]
   [astronomy.scripts.test-conn :refer [test-db11]]))


;; test model

(def clock
  (d/pull test-db11 '[*] [:clock/name "default"]))

(def earth
  (d/pull test-db11 '[* {:celestial/clock [*]}] [:planet/name "earth"]))

(def moon
  (d/pull test-db11 '[*] [:satellite/name "moon"]))


(deftest test-celestial
  (is (celestial-group.m/update-current-position-tx
       test-db11
       {:db/id [:entity/chinese-name "地月系"]})
      [{:db/id [:entity/chinese-name "地月系"], :object/position '(442.48929246587807 191.5505796685996 -88.92178938105835)}]))


(run-tests)
