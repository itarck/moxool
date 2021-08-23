(ns astronomy.model.test-celestial
  (:require
   [cljs.spec.alpha :as s]
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [datascript.core :as d]
   [shu.three.vector3 :as v3]
   [astronomy.model.celestial :as m.celestial]
   [astronomy.scripts.test-conn :refer [test-db3 test-db11]]))


;; test model

(def test-conn (d/conn-from-db test-db11))

(def clock
  (d/pull test-db11 '[*] [:clock/name "default"]))

(def earth
  (d/pull test-db11 '[* {:celestial/clock [*]}] [:planet/name "earth"]))


(deftest test-celestial
  (let [length (->> (m.celestial/cal-position earth 0)
                    (v3/from-seq)
                    (v3/length))]
    (is (s/valid? :methodology/entity earth))
    (is (s/valid? :astronomy/celestial earth))
    (is (and
         (> length 480) (< length 510)))))


(map #(m.celestial/cal-position earth %) (range 10))


(run-tests)



