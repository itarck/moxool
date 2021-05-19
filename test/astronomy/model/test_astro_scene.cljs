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
    (d/pull db '[*] [:scene/name "solar"]))

  (def clock1
    (d/pull db '[*] [:clock/name "default"]))

  (m.astro-scene/update-by-clock-tx
   db (:db/id astro-scene1) (:db/id clock1))

;;   
  )