(ns astronomy.data.backpack
  (:require
   [methodology.model.user.backpack :as m.backpack]))


(def dataset1
  (let [bp {:db/id [:backpack/name "default"]}]
    [(m.backpack/put-in-cell-tx bp 0 {:db/id [:tool/name "ppt tool"]})
     (m.backpack/put-in-cell-tx bp 1 {:db/id [:tool/name "clock control 1"]})
     (m.backpack/put-in-cell-tx bp 2 {:db/id [:tool/name "goto celestial tool"]})
     (m.backpack/put-in-cell-tx bp 3 {:db/id [:tool/name "spaceship camera tool"]})
     (m.backpack/put-in-cell-tx bp 4 {:db/id [:tool/name "horizon-coordinate-tool"]})
     (m.backpack/put-in-cell-tx bp 5 {:db/id [:tool/name "terrestrial-coordinate-tool"]})
     (m.backpack/put-in-cell-tx bp 6 {:db/id [:tool/name "constellation-tool"]})
     (m.backpack/put-in-cell-tx bp 7 {:db/id [:tool/name "universe tool"]})
     (m.backpack/put-in-cell-tx bp 8 {:db/id [:tool/name "atmosphere-tool"]})
     (m.backpack/put-in-cell-tx bp 9 {:db/id [:tool/name "eagle-eye-tool"]})
     (m.backpack/put-in-cell-tx bp 11 {:db/id [:tool/name "astronomical-coordinate-tool"]})]))


dataset1
