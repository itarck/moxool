(ns astronomy.model.test-satellite
  (:require
   [shu.three.matrix4 :as m4]
   [astronomy.test-conn :refer [create-poshed-conn!]]
   [astronomy.model.satellite :as m.satellite]))


(def test-conn (create-poshed-conn!))


(def moon-id [:satellite/name "moon"])

(def satellite-2 (m.satellite/pull-satellite-fully @test-conn moon-id))

satellite-2

(m.satellite/cal-world-position satellite-2)


