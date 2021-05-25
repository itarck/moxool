(ns astronomy.model.test-astro-scene
  (:require
   [datascript.core :as d]
   [cljs.spec.alpha :as s]
   [astronomy.test-conn :refer [create-test-conn!]]
   [astronomy.model.astro-scene :as m.astro-scene]))


(comment
  (def conn (create-test-conn!))

  (def db @conn)

  (def astro-scene1
    (d/pull db '[* {:object/_scene [*]}] [:scene/name "solar"]))

  (d/pull db '[*] [:star/name "sun"])
  
  (map :entity/chinese-name (:object/_scene astro-scene1))

  (def clock1
    (d/pull db '[*] [:clock/name "default"]))

  (m.astro-scene/update-by-clock-tx
   db (:db/id astro-scene1) (:db/id clock1))



;;   
  )