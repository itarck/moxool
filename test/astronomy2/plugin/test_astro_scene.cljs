(ns astronomy2.plugin.test-astro-scene
  (:require
   [astronomy2.plugin.astro-scene]
   [astronomy2.app :as app]))


;; view

(comment

  (let [tx [#:scene {:name ::scene
                     :background "black"
                     :type :astro-scene
                     :scale [3 3 3]}
            #:object {:name "object1"
                      :scene [:scene/name ::scene]
                      :type :box
                      :position [0 0 0]
                      :rotation [0 0 0]
                      :scale [1 1 1]
                      :box/color "red"}
            #:framework{:name "default"
                        :scene [:scene/name ::scene]}]]
    (app/homies :transact! tx))

;; 
  )