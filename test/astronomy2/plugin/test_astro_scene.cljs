(ns astronomy2.plugin.test-astro-scene
  (:require
   [astronomy2.plugin.astro-scene]
   [astronomy2.app :as app]))



;; view

(comment

  (def tx
    [#:scene {:name ::scene
              :background "black"
              :type :astro-scene
              :scale [10 10 10]}
     #:object {:scene [:scene/name ::scene]
               :position [0 0 0]
               :rotation [0 0 0]
               :scale [1 1 1]}
     #:framework{:name "default"
                 :scene [:scene/name ::scene]}])

  (app/app-transact! tx)

;; 
  )